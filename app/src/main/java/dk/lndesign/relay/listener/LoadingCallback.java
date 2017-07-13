package dk.lndesign.relay.listener;

import android.support.annotation.NonNull;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public interface LoadingCallback<T> {

    void onDataLoaded(@NonNull T response, boolean isFromCache);
    void onLoadingFailed();
}
