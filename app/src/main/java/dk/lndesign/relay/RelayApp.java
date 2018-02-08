package dk.lndesign.relay;

import android.app.Application;

import dk.lndesign.relay.api.TwitchApiService;
import dk.lndesign.relay.api.TwitchApiServiceBuilder;
import timber.log.Timber;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class RelayApp extends Application {

    public static final String API_TWITCH_BASE_URL = "https://api.twitch.tv/kraken/";

    private static final String API_TWITCH_CACHE_DIR = "api_cache_twitch";

    private static RelayApp sInstance;
    private TwitchApiService mTwitchApiService;

    public static RelayApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mTwitchApiService = TwitchApiServiceBuilder.createRetrofit(this, API_TWITCH_BASE_URL, API_TWITCH_CACHE_DIR).create(TwitchApiService.class);
    }

    public TwitchApiService getTwitchApiService() {
        return mTwitchApiService;
    }
}
