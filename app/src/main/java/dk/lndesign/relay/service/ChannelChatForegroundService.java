package dk.lndesign.relay.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import dk.lndesign.relay.Constants;
import dk.lndesign.relay.MainActivity;
import dk.lndesign.relay.R;
import dk.lndesign.relay.irc.IRCReturnCode;
import dk.lndesign.relay.model.IRCMessage;
import dk.lndesign.relay.model.Stream;
import timber.log.Timber;

/**
 * Foreground service for fetching Twitch IRC chat for Twitch channel.
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class ChannelChatForegroundService extends Service {

    private volatile boolean running = true;

    private Stream mSelectedStream;

    @Override
    public void onCreate() {
        super.onCreate();

        Thread ircThread = new Thread(new Runnable() {
            BufferedWriter writer;

            @Override
            public void run() {
                try {
                    // Connect.
                    Socket socket = new Socket(Constants.Twitch.Irc.SERVER, Constants.Twitch.Irc.PORT);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Login.
                    writer.write("PASS oauth:" + Constants.Twitch.ACCESS_TOKEN + "\r\n");
                    writer.write("NICK " + Constants.Twitch.NICK + "\r\n");
                    writer.flush();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(IRCReturnCode.RPL_MYINFO)) {
                            // We are now logged in.
                            Timber.d("Logged in");
                            break;
                        } else if (line.contains(IRCReturnCode.ERR_NICKNAMEINUSE)) {
                            Timber.e("Nickname is already in use.");
                            return;
                        }
                    }

                    // Join.
                    writer.write("JOIN #" + mSelectedStream.getChannel().getName() + "\r\n");
                    writer.flush();

                    // Read lines.
                    while ((line = reader.readLine()) != null) {
                        if (!running) {
                            // Departure from channel and stop thread.
                            // Departure will one happen on new output lines from IRC channel.
                            // TODO: Trigger departure ASAP, not only on new lines.
                            Timber.d("Departing channel: %s", mSelectedStream.getChannel().getName());
                            writer.write("PART " + mSelectedStream.getChannel().getName());
                            return;
                        }

                        IRCMessage ircMessage = new IRCMessage(line);

                        if (ircMessage.getCommand() != null &&
                                ircMessage.getCommand().equalsIgnoreCase("PRIVMSG")) {
                            Timber.i(ircMessage.toString());
                        } else {
                            Timber.w(ircMessage.getRaw());
                        }

                        // Respond to ping to avoid being disconnected.
                        if (ircMessage.getCommand() != null &&
                                ircMessage.getCommand().equalsIgnoreCase("PING")) {
                            Timber.d("PONG :%s", ircMessage.getMessage());

                            writer.write("PONG " + ircMessage.getRaw().substring(5) + "\r\n");
                            writer.flush();
                        }
                    }

                } catch (IOException e) {
                    Timber.e("Could not establish connection to server: %s", Constants.Twitch.Irc.SERVER);
                }
            }
        });
        ircThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null &&
                intent.getAction().equals(Constants.Action.START_FOREGROUND_ACTION)) {
            Timber.d("Starting foreground service");

            mSelectedStream = intent.getParcelableExtra(Constants.Key.SELECTED_STREAM);

            // Setup service notification.
            // TODO: Open chat with selected channel, instead of of opening main activity.
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.Action.FOREGROUND_MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent disconnectChatIntent = new Intent(this, ChannelChatForegroundService.class);
            disconnectChatIntent.setAction(Constants.Action.STOP_FOREGROUND_ACTION);
            PendingIntent pendingDisconnectChatIntent = PendingIntent.getService(this, 0, disconnectChatIntent, 0);

            // Build notification.
            final NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(
                            this, this.getString(R.string.test_notification_channel_id))
                    .setContentTitle("Relay Persistent Chat")
                    .setTicker("Relay Persistent Chat")
                    .setContentText(Constants.Twitch.CHANNEL)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    // TODO: Use channel icon.
                    .setLargeIcon(
                            BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.ic_launcher_round))
                    .addAction(R.drawable.ic_close_black_24dp,
                            "Disconnect",
                            pendingDisconnectChatIntent)
                    .setOngoing(true);

            // Replace large notification icon if we have a channel logo.
            if (mSelectedStream != null && mSelectedStream.getChannel().getLogo() != null) {
                // Add channel name.
                notificationBuilder.setContentText(mSelectedStream.getChannel().getDisplayName());

                // Add channel logo.
                Glide.with(getApplicationContext())
                        .load(mSelectedStream.getChannel().getLogo())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                notificationBuilder.setLargeIcon(resource);
                            }
                        });
            }

            // Start foreground service with notification.
            running = true;
            startForeground(Constants.Notification.FOREGROUND_SERVICE_ID, notificationBuilder.build());

        } else if (intent.getAction().equals(Constants.Action.STOP_FOREGROUND_ACTION)) {
            Timber.d("Stopping foreground service");

            // Stop notification and service.
            running = false;
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
