
package com.tamic.image.glidewarpper.cache;

import android.content.Context;
import android.support.annotation.NonNull;


import com.tamic.image.glidewarpper.core.ImageResponseBody;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * cache Interceptor
 * Created by Tamic on 2016-08-31.
 */
public class CaheInterceptor implements Interceptor {

    protected static String TAG = "TamicImageLoerCache";

    private Context context;

    private ImageResponseBody.ProgressListener listener;

    public CaheInterceptor(@NonNull Context context, ImageResponseBody.ProgressListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        /*Request request = chain.request();
        String url = request.url().url().toString();
        Log.v(TAG, "load url : " + url);
        if (NetworkUtil.isNetworkAvailable(context)) {
            Response response = chain.proceed(request);
            // read from cache for 300 s
            int maxAge = ImageCacheManager.ONLINE_MAXS_TALE;
            String cacheControl = request.cacheControl().toString();
            Log.v(TAG, maxAge + "s load cache: " + cacheControl);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    //.header("Cache-Control", String.format("max-age=%d", maxAge))
                    .body(new ImageResponseBody(response.body(), url, listener))
                    .build();
        } else {
            Log.v(TAG, "no network load cache");
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            int maxStale = ImageCacheManager.OFFE_LINE_MAX_STALE;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .body(new ImageResponseBody(response.body(), url, listener))
                    .build();
        }*/

        //每一张图片的url永远不可能被复用，所以没有必要做缓存有效期的判断

        HttpUrl httpUrl = chain.request().url();
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        String shortUrl = httpUrl.scheme() + "://" + httpUrl.host() + httpUrl.encodedPath();
        String query = ImageUrlQueryMap.getQuery(shortUrl);
        if (query != null) {
            ImageUrlQueryMap.removeQuery(shortUrl);
            urlBuilder.query(query);
            Request newRequest = chain.request().newBuilder().url(urlBuilder.build()).build();
            Response response = chain.proceed(newRequest);
            Request.Builder reqBuilder = response.request().newBuilder();
            reqBuilder.url(httpUrl);
            Response.Builder resBuilder = response.newBuilder();

            response = resBuilder
                    .request(reqBuilder.build())
                    .body(new ImageResponseBody(response.body(), httpUrl.toString(), listener))
                    .build();

            return response;
        }

        return chain.proceed(chain.request());
    }
}
