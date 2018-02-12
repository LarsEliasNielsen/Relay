package dk.lndesign.relay.api

import dk.lndesign.relay.Constants
import dk.lndesign.relay.model.FollowedChannels
import dk.lndesign.relay.model.Stream
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface for making API requests.
 *
 *
 * Cache-Control headers of requests will be appended to the responses and cached accordingly.
 * More about cache parameters here:
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
 */
interface TwitchApiService {

    @Headers(ACCEPT + ": application/vnd.twitchtv.v3+json",
            AUTHORIZATION + ": OAuth " + Constants.Twitch.ACCESS_TOKEN,
            RESPONSE_CACHE_HEADER + ": max-age=" + MINUTE,
            CACHE_CONTROL + ": max-stale=" + HOUR)
    @GET("streams/{channel}")
    fun getStream(@Path("channel") channel: String): Call<Stream.Wrapper>

    /**
     * Fetch a list of followed streams.
     *
     * {@see https://dev.twitch.tv/docs/v3/reference/users#get-streamsfollowed}
     * @param streamType Type of streams (all, playlist, live).
     * @param limit Limit of streams, default is 25, max is 100.
     * @return Followed streams.
     */
    @Headers(ACCEPT + ": application/vnd.twitchtv.v3+json",
            AUTHORIZATION + ": OAuth " + Constants.Twitch.ACCESS_TOKEN,
            RESPONSE_CACHE_HEADER + ": max-age=" + MINUTE,
            CACHE_CONTROL + ": max-stale=" + HOUR)
    @GET("streams/followed")
    fun getFollowedChannels(@Query("stream_type") streamType: String,
                            @Query("limit") limit: Int):
            // TODO: Add IntRange and and StringDef.
            Call<FollowedChannels>

    companion object {

        // Time periods in seconds.
        const val MINUTE = 60
        const val HOUR = 60 * 60
        const val HALF_DAY = 12 * HOUR
        const val DAY = 24 * HOUR

        const val ACCEPT = "Accept"
        const val AUTHORIZATION = "Authorization"
        const val RESPONSE_CACHE_HEADER = "response-cache" // This header will be inserted into responses to override server.
        const val CACHE_CONTROL = "Cache-Control"
    }
}
