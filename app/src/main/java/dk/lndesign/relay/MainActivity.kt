package dk.lndesign.relay

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import dk.lndesign.relay.adapter.ChannelListAdapter
import dk.lndesign.relay.api.TwitchController
import dk.lndesign.relay.listener.LoadingCallback
import dk.lndesign.relay.model.FollowedChannels
import dk.lndesign.relay.model.Stream
import dk.lndesign.relay.service.ChannelChatForegroundService
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val mTwitchController = TwitchController()
    private var mSelectedStream: Stream? = null
    private var mFollowedChannels: List<Stream>? = null

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mAdapter: ChannelListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startForegroundServiceButton = findViewById<Button>(R.id.foreground_service_start)
        val stopForegroundServiceButton = findViewById<Button>(R.id.foreground_service_stop)

        startForegroundServiceButton.setOnClickListener {
            val startIntent = Intent(this, ChannelChatForegroundService::class.java)
            startIntent.action = Constants.Action.START_FOREGROUND_ACTION
            startIntent.putExtra(Constants.Key.SELECTED_STREAM, mSelectedStream)
            startService(startIntent)
        }
        stopForegroundServiceButton.setOnClickListener {
            val stopIntent = Intent(this, ChannelChatForegroundService::class.java)
            stopIntent.action = Constants.Action.STOP_FOREGROUND_ACTION
            startService(stopIntent)
        }

        // TODO: Only fetch live channels.
        mTwitchController.loadFollowedStreams(object : LoadingCallback<FollowedChannels> {
            override fun onDataLoaded(response: FollowedChannels, isFromCache: Boolean) {
                mFollowedChannels = response.streams
                mAdapter!!.updateItems(mFollowedChannels!!)

                for (stream in mFollowedChannels!!) {
                    Timber.d(stream.toString())

                    if (Constants.Twitch.CHANNEL == "#" + stream.channel.name) {
                        mSelectedStream = stream
                    }
                }
            }

            override fun onLoadingFailed() {
                Timber.e("Could not load followed channels")
            }
        })

        mRecyclerView = findViewById(R.id.recycler_view_channels)

        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mLayoutManager

        mAdapter = ChannelListAdapter()
        mRecyclerView!!.adapter = mAdapter
    }
}
