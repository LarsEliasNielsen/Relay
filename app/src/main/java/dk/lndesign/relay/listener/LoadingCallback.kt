package dk.lndesign.relay.listener

interface LoadingCallback<T> {
    fun onDataLoaded(response: T, isFromCache: Boolean)
    fun onLoadingFailed()
}
