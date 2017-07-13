package dk.lndesign.relay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import dk.lndesign.relay.service.ChannelChatForegroundService;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

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
    }
}
