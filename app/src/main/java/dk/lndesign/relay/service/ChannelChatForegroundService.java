package dk.lndesign.relay.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

/**
 * Foreground service for fetching Twitch IRC chat for Twitch channel.
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class ChannelChatForegroundService extends Service {

    private static final String LOG_TAG = ChannelChatForegroundService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect.
                    Socket socket = new Socket(Constants.Twitch.Irc.SERVER, Constants.Twitch.Irc.PORT);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Login.
                    writer.write("PASS oauth:" + Constants.Twitch.ACCESS_TOKEN + "\r\n");
                    writer.write("NICK " + Constants.Twitch.NICK + "\r\n");
                    writer.flush();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(IRCReturnCode.RPL_MYINFO)) {
                            // We are now logged in.
                            Log.d(LOG_TAG, "Logged in");
                            break;
                        } else if (line.contains(IRCReturnCode.ERR_NICKNAMEINUSE)) {
                            Log.e(LOG_TAG, "Nickname is already in use.");
                            return;
                        }
                    }

                    // Join.
                    writer.write("JOIN " + Constants.Twitch.CHANNEL + "\r\n");
                    writer.flush();

                    // Read lines.
                    while ((line = reader.readLine()) != null) {
                        IRCMessage ircMessage = new IRCMessage(line);

                        if (ircMessage.getCommand() != null &&
                                ircMessage.getCommand().equalsIgnoreCase("PRIVMSG")) {
                            Log.i("IRC", ircMessage.toString());
                        } else {
                            Log.w("IRC", ircMessage.getRaw());
                        }

                        // Respond to ping to avoid being disconnected.
                        if (ircMessage.getCommand() != null &&
                                ircMessage.getCommand().equalsIgnoreCase("PING")) {
                            Log.d("IRC", "PONG " + ircMessage.getMessage());

                            writer.write("PONG " + ircMessage.getRaw().substring(5) + "\r\n");
                            writer.flush();
                        }
                    }

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not establish connection to server: " + Constants.Twitch.Irc.SERVER);
                }
            }
        });
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.Action.START_FOREGROUND_ACTION)) {
            Log.d(LOG_TAG, "Starting foreground service");

            // Setup service notification.
            // TODO: Open chat with selected channel, instead of of opening main activity.
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.Action.FOREGROUND_MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            // Build notification.
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Relay Persistent Chat")
                    .setTicker("Relay Persistent Chat")
                    .setContentText(Constants.Twitch.CHANNEL)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    // TODO: Use channel icon.
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setOngoing(true)
                    .build();

            // Start foreground service with notification.
            startForeground(Constants.Notification.FOREGROUND_SERVICE_ID, notification);

        } else if (intent.getAction().equals(Constants.Action.STOP_FOREGROUND_ACTION)) {
                Log.d(LOG_TAG, "Stopping foreground service");

                // Stop notification and service.
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
