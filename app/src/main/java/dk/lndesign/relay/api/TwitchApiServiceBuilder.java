package dk.lndesign.relay.api;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import dk.lndesign.relay.util.NetworkUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/***************************************************************************************************
 * CONSTRUCTION ZONE: CONVERTING TO KOTLIN                                                         *
 **************************************************************************************************/
/**
 * Util class used for setting up Retrofit.
 */
public class TwitchApiServiceBuilder {

    /**
     * Initializes Retrofit with Gson converter and cache directory.
     */
    @NonNull
    public static Retrofit createRetrofit(Context context, String baseUrl, String cacheDirName) {
        return createRetrofit(baseUrl, createCachedClient(context, cacheDirName));
    }

    /**
     * Initializes Retrofit with Gson converter and specified cache.
     */
    @NonNull
    public static Retrofit createRetrofit(String baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Creates an OkHttpClient with a cache file.
     * <p/>
     * All requests are intercepted and response cache headers are changed to request cache headers
     * for simple cache control.
     */
    @NonNull
    public static OkHttpClient createCachedClient(final Context context, String cacheDirName) {
        File httpCacheDirectory = new File(context.getCacheDir(), cacheDirName);

        Cache cache = new Cache(httpCacheDirectory, 20 * 1024 * 1024);

        return new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        boolean isOnNetwork = NetworkUtil.isNetworkAvailable(context);

                        // If the network is not available, force cache response.
                        if (!isOnNetwork) {
                            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                        }

                        // Get the response.
                        Response response = chain.proceed(request);

                        // If the network is available and the response is stale, try again with forced network.
                        if (isOnNetwork && "110 HttpURLConnection \"Response is stale\"".equals(response.header("Warning"))) {
                            request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
                            response = chain.proceed(request);
                        }

                        // Check response after using force cache
                        if (!isOnNetwork && response.code() == 504) {
                            // Throw if Unsatisfiable Request (only-if-cached)
                            throw new IOException(response.message());
                        }

                        return response;
                    }
                })
                .addNetworkInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                // swap response cache headers for request cache headers
                                Request request = chain.request();
                                String cacheHeader = request.header(TwitchApiService.RESPONSE_CACHE_HEADER);
                                Request processedRequest = request.newBuilder().removeHeader(TwitchApiService.RESPONSE_CACHE_HEADER).build();
                                Response response = chain.proceed(processedRequest);

                                response = response.newBuilder()
                                        .removeHeader("Pragma") // remove cache headers that could mess up our caching
                                        .removeHeader("Vary")
                                        .removeHeader("X-Varnish-Cache-Control")
                                        .removeHeader("Age")
                                        .header("Cache-Control", cacheHeader)
                                        .build();

                                return response;
                            }
                        }
                ).build();
    }
}
