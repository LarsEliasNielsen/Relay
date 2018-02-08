package dk.lndesign.relay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import dk.lndesign.relay.api.TwitchController;
import dk.lndesign.relay.listener.LoadingCallback;
import dk.lndesign.relay.model.Stream;
import dk.lndesign.relay.service.ChannelChatForegroundService;
import timber.log.Timber;

public class ChatActivity extends AppCompatActivity {

    private TwitchController mTwitchController = new TwitchController();

    public static Intent newIntent(@NonNull Context context, String channel) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.Key.SELECTED_CHANNEL_NAME, channel);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String channel = getIntent().getStringExtra(Constants.Key.SELECTED_CHANNEL_NAME);

        if (channel != null) {
            mTwitchController.loadStream(channel, new LoadingCallback<Stream.Wrapper>() {
                @Override
                public void onDataLoaded(@NonNull Stream.Wrapper response, boolean isFromCache) {
                    Intent startIntent = new Intent(ChatActivity.this, ChannelChatForegroundService.class);
                    startIntent.setAction(Constants.Action.START_FOREGROUND_ACTION);
                    startIntent.putExtra(Constants.Key.SELECTED_STREAM, response.getStream());
                    startService(startIntent);
                }

                @Override
                public void onLoadingFailed() {
                    Timber.e("Failed to load stream");
                }
            });
        } else {
            Timber.e("No channel provided, please use #netIntent(Context, String)");
        }
    }
}
