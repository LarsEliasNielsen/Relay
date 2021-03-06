package dk.lndesign.relay.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dk.lndesign.relay.RelayApp;
import dk.lndesign.relay.listener.LoadingCallback;
import dk.lndesign.relay.model.FollowedChannels;
import dk.lndesign.relay.model.Stream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class TwitchController {

    private TwitchApiService mTwitchApiService;
    private List<Call<?>> mCallList = new ArrayList<>();

    public TwitchController() {
        mTwitchApiService = RelayApp.getInstance().getTwitchApiService();
    }

    public void loadStream(@NonNull String channel, @NonNull LoadingCallback<Stream.Wrapper> callback) {
        Call<Stream.Wrapper> call = mTwitchApiService.getStream(channel);

        makeRequest("Stream [" + channel + "]", call, callback);
    }

    public void loadFollowedStreams(@NonNull LoadingCallback<FollowedChannels> callback) {
        Call<FollowedChannels> call = mTwitchApiService.getFollowedChannels("all", 100);

        makeRequest("Followed Twitch streams [all]", call, callback);
    }

    public void loadLiveFollowedStreams(@NonNull LoadingCallback<FollowedChannels> callback) {
        Call<FollowedChannels> call = mTwitchApiService.getFollowedChannels("live", 100);

        makeRequest("Followed Twitch streams [live]", call, callback);
    }

    /**
     * Enqueues the provided call and invokes the provided callback on success or on error.
     * Doesn't invoke the callback if the call was canceled, so it's safe to manipulate UI from there.
     *
     * @param requestName Request name for logging.
     * @param call        Call to enqueue.
     * @param callback    Callback to invoke on response.
     */
    private <T> void makeRequest(final @NonNull String requestName, @NonNull Call<T> call,
                                 final @NonNull LoadingCallback<T> callback) {
        // Add the call to our list to make it stoppable.
        mCallList.add(call);

        // Request.
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (call.isCanceled() || response.body() == null) {
                    handleError(requestName, response, call, null, callback);
                } else {
                    callback.onDataLoaded(response.body(), response.raw().request().cacheControl().onlyIfCached());
                }
                mCallList.remove(call);
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                handleError(requestName, null, call, t, callback);
                mCallList.remove(call);
            }
        });
    }

    /**
     * Cancel all existing requests.
     */
    public void cancelRequests() {
        for (Call<?> call : mCallList) {
            call.cancel();
        }
        mCallList.clear();
    }

    /**
     * Handles error from request.
     *
     * @param identification Some unique string identifying the request.
     * @param response       Response of the call, can be null.
     * @param call           Call that failed.
     * @param throwable      Exception that was thrown.
     * @param callback       Callback to invoke if failed.
     */
    private void handleError(@NonNull String identification, @Nullable Response response,
                             @NonNull Call call, @Nullable Throwable throwable,
                             @NonNull LoadingCallback callback) {

        if (call.isCanceled()) {
            Timber.e("%s request was canceled", identification);
        } else {
            if (response != null) {
                Timber.e("%s request failed with: %s", identification, response.message());

            } else {
                Timber.e("%s request failed", identification);
            }

            Timber.e(throwable, "Url: %s", call.request().url());

            // Only invoke callback if the call wasn't canceled
            callback.onLoadingFailed();
        }
    }
}
