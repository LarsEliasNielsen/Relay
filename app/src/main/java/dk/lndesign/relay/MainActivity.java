package dk.lndesign.relay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import dk.lndesign.relay.adapter.ChannelListAdapter;
import dk.lndesign.relay.api.TwitchController;
import dk.lndesign.relay.listener.LoadingCallback;
import dk.lndesign.relay.model.FollowedChannels;
import dk.lndesign.relay.model.Stream;
import dk.lndesign.relay.service.ChannelChatForegroundService;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TwitchController mTwitchController = new TwitchController();
    private Stream mSelectedStream;
    private List<Stream> mFollowedChannels;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChannelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startForegroundServiceButton = (Button) findViewById(R.id.foreground_service_start);
        Button stopForegroundServiceButton = (Button) findViewById(R.id.foreground_service_stop);

        startForegroundServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(MainActivity.this, ChannelChatForegroundService.class);
                startIntent.setAction(Constants.Action.START_FOREGROUND_ACTION);
                startIntent.putExtra(Constants.Key.SELECTED_STREAM, mSelectedStream);
                startService(startIntent);
            }
        });
        stopForegroundServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(MainActivity.this, ChannelChatForegroundService.class);
                stopIntent.setAction(Constants.Action.STOP_FOREGROUND_ACTION);
                startService(stopIntent);
            }
        });

        // TODO: Only fetch live channels.
        mTwitchController.loadFollowedChannels(new LoadingCallback<FollowedChannels>() {
            @Override
            public void onDataLoaded(@NonNull FollowedChannels response, boolean isFromCache) {
                mFollowedChannels = response.getStreams();
                mAdapter.updateItems(mFollowedChannels);

                for (Stream stream : mFollowedChannels) {
                    Log.d(LOG_TAG, stream.toString());

                    if (Constants.Twitch.CHANNEL.equals("#" + stream.getChannel().getName())) {
                        mSelectedStream = stream;
                    }
                }
            }

            @Override
            public void onLoadingFailed() {
                Log.e(LOG_TAG, "Could not load followed channels");
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_channels);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChannelListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
}
