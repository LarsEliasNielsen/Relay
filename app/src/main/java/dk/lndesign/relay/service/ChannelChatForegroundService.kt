package dk.lndesign.relay.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.app.NotificationCompat

import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

import dk.lndesign.relay.Constants
import dk.lndesign.relay.MainActivity
import dk.lndesign.relay.R
import dk.lndesign.relay.irc.IRCReturnCode
import dk.lndesign.relay.model.IRCMessage
import dk.lndesign.relay.model.Stream
import timber.log.Timber

/**
 * Foreground service for fetching Twitch IRC chat for Twitch channel.
 */
class ChannelChatForegroundService : Service() {

    @Volatile private var running = true

    private var mSelectedStream: Stream? = null

    override fun onCreate() {
        super.onCreate()

        val ircThread = Thread(object : Runnable {

            override fun run() {
                try {
                    // Get writer.
                    var writer: BufferedWriter
                    val socket = Socket(Constants.Twitch.Irc.SERVER, Constants.Twitch.Irc.PORT)
                    writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                    // Login.
                    writer.write("PASS oauth:" + Constants.Twitch.ACCESS_TOKEN + "\r\n")
                    writer.write("NICK " + Constants.Twitch.NICK + "\r\n")
                    writer.flush()

                    // Get reader.
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    reader.lineSequence().forEach {
                        when {
                            it.contains(IRCReturnCode.RPL_MYINFO) -> Timber.i("Logged in")
                            it.contains(IRCReturnCode.ERR_NICKNAMEINUSE) -> Timber.w("Nickname is already in use.")
                        }
                    }

                    // Join.
                    writer.write("JOIN #${mSelectedStream!!.channel.name}\r\n")
                    writer.flush()

                    // Read lines.
                    reader.lineSequence().forEach {
                        if (!running) {
                            // Departure from channel and stop thread.
                            // Departure will one happen on new output lines from IRC channel.
                            // TODO: Trigger departure ASAP, not only on new lines.
                            Timber.d("Departing channel: %s", mSelectedStream!!.channel.name)
                            writer.write("PART " + mSelectedStream!!.channel.name)
                            return
                        }

                        val ircMessage = IRCMessage(it)

                        if (ircMessage.command != null && ircMessage.command.equals("PRIVMSG", ignoreCase = true)) {
                            Timber.i(ircMessage.toString())
                        } else {
                            Timber.w(ircMessage.raw)
                        }

                        // Respond to ping to avoid being disconnected.
                        if (ircMessage.command != null && ircMessage.command.equals("PING", ignoreCase = true)) {
                            Timber.d("PONG :%s", ircMessage.message)

                            writer.write("PONG " + ircMessage.raw.substring(5) + "\r\n")
                            writer.flush()
                        }
                    }

                } catch (e: IOException) {
                    Timber.e("Could not establish connection to server: %s", Constants.Twitch.Irc.SERVER)
                }

            }
        })
        ircThread.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action != null && intent.action == Constants.Action.START_FOREGROUND_ACTION) {
            Timber.d("Starting foreground service")

            mSelectedStream = intent.getParcelableExtra(Constants.Key.SELECTED_STREAM)

            // Setup service notification.
            // TODO: Open chat with selected channel, instead of of opening main activity.
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.action = Constants.Action.FOREGROUND_MAIN_ACTION
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

            val disconnectChatIntent = Intent(this, ChannelChatForegroundService::class.java)
            disconnectChatIntent.action = Constants.Action.STOP_FOREGROUND_ACTION
            val pendingDisconnectChatIntent = PendingIntent.getService(this, 0, disconnectChatIntent, 0)

            // Build notification.
            val notificationBuilder = NotificationCompat.Builder(this, Constants.Twitch.CHANNEL)
                    .setContentTitle("Relay Persistent Chat")
                    .setTicker("Relay Persistent Chat")
                    .setContentText(Constants.Twitch.CHANNEL)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    // TODO: Use channel icon.
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                    .addAction(R.drawable.ic_close_black_24dp,
                            "Disconnect",
                            pendingDisconnectChatIntent)
                    .setOngoing(true)

            // Replace large notification icon if we have a channel logo.
            if (mSelectedStream != null && mSelectedStream!!.channel.logo != null) {
                // Add channel name.
                notificationBuilder.setContentText(mSelectedStream!!.channel.displayName)

                // Add channel logo.
                Glide.with(applicationContext)
                        .load(mSelectedStream!!.channel.logo)
                        .asBitmap()
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                                notificationBuilder.setLargeIcon(resource)
                            }
                        })
            }

            // Start foreground service with notification.
            running = true
            startForeground(Constants.Notification.FOREGROUND_SERVICE_ID, notificationBuilder.build())

        } else if (intent.action == Constants.Action.STOP_FOREGROUND_ACTION) {
            Timber.d("Stopping foreground service")

            // Stop notification and service.
            running = false
            stopForeground(true)
            stopSelf()
        }

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // Used only in case of bound services.
        return null
    }
}
