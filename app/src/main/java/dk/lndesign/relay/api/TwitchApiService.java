package dk.lndesign.relay.api;

import dk.lndesign.relay.Constants;
import dk.lndesign.relay.model.FollowedChannels;
import dk.lndesign.relay.model.Stream;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for making API requests.
 * <p/>
 * Cache-Control headers of requests will be appended to the responses and cached accordingly.
 * More about cache parameters here:
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
 *
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public interface TwitchApiService {

    // Time periods in seconds.
    int MINUTE = 60;
    int HOUR = 60 * 60;
    int HALF_DAY = 12 * HOUR;
    int DAY = 24 * HOUR;

    String ACCEPT = "Accept";
    String AUTHORIZATION = "Authorization";
    String RESPONSE_CACHE_HEADER = "response-cache"; // This header will be inserted into responses to override server.
    String CACHE_CONTROL = "Cache-Control";

    @Headers({
            ACCEPT + ": application/vnd.twitchtv.v3+json",
            AUTHORIZATION + ": OAuth " + Constants.Twitch.ACCESS_TOKEN,
            RESPONSE_CACHE_HEADER + ": max-age=" + MINUTE,
            CACHE_CONTROL + ": max-stale=" + HOUR
    })
    @GET("streams/{channel}")
    Call<Stream.Wrapper> getStream(@Path("channel") String channel);

    /**
     * Fetch a list of followed streams.
     *
     * {@see https://dev.twitch.tv/docs/v3/reference/users#get-streamsfollowed}
     * @param streamType Type of streams (all, playlist, live).
     * @param limit Limit of streams, default is 25, max is 100.
     * @return Followed streams.
     */
    @Headers({
            ACCEPT + ": application/vnd.twitchtv.v3+json",
            AUTHORIZATION + ": OAuth " + Constants.Twitch.ACCESS_TOKEN,
            RESPONSE_CACHE_HEADER + ": max-age=" + MINUTE,
            CACHE_CONTROL + ": max-stale=" + HOUR
    })
    @GET("streams/followed")
    // TODO: Add IntRange and and StringDef.
    Call<FollowedChannels> getFollowedChannels(@Query("stream_type") String streamType,
                                               @Query("limit") int limit);
}
