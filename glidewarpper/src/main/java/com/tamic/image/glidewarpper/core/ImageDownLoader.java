package com.tamic.image.glidewarpper.core;


import android.net.Uri;
import android.util.Log;


import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;
import com.tamic.image.glidewarpper.cache.ImageUrlQueryMap;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * 为了让 picasso兼容okhttp 3.8, 自己实现 ImageDownLoader
 * <p/>
 * Created by Tamic on 2016-08-31.
 */
public class ImageDownLoader implements Downloader {

    private static final String TAG = "ImageDownLoader";
    OkHttpClient client = null;

    public ImageDownLoader(OkHttpClient client) {
        this.client = client;
    }


    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        uri = ImageUrlQueryMap.saveQuery(uri);
        Log.i(TAG, "load image downloader url->" + uri + "  cacheControl cache" + networkPolicy);
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                CacheControl.Builder builder = new CacheControl.Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }

        Request.Builder server_builder = new Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            server_builder.cacheControl(cacheControl);
        }
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = client.newCall(server_builder.build()).execute();
        int responseCode = response.code();
        Log.i(TAG, "code：" + responseCode);

        if (responseCode >= 300) {
            response.body().close();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy, responseCode);
        }

        boolean serverfromCache = response.cacheResponse() != null;
        Log.i(TAG, "is load Cache? ->:" + serverfromCache);
        Log.i(TAG, "header->:" + response.headers());

        if ("text/html".equals(response.header("Content-Type")) || "text/plain".equals(response.header("Content-Type"))) {
            //服务端返回的是图片的url
            Log.i(TAG, "server response is url , not image source");
            String cdnUrl = response.body().string();
            Log.i(TAG, "server response time:" + (System.currentTimeMillis() - startTime));
            Log.i(TAG, "cdn imageUrl->" + cdnUrl);
            // 构建CDN控制器
            Request.Builder cdn_builder = new Request.Builder().url(cdnUrl);
            if (cacheControl != null) {
                cdn_builder.cacheControl(cacheControl);
            }
            long cdnStartTime = System.currentTimeMillis();
            okhttp3.Response cdn_response = client.newCall(cdn_builder.build()).execute();
            int cdn_responseCode = cdn_response.code();
            Log.i(TAG, "cdn code:" + cdn_responseCode);

            if (cdn_responseCode >= 300) {
                cdn_response.body().close();
                throw new ResponseException(cdn_responseCode + " " + cdn_response.message(), networkPolicy,
                        cdn_responseCode);
            }
            Log.i(TAG, "cdn响应时间" + (System.currentTimeMillis() - cdnStartTime));
            boolean cnd_fromCache = cdn_response.cacheResponse() != null;
            ResponseBody cdn_responseBody = cdn_response.body();
            Log.i(TAG, " cdn loadimage is fromCache？->" + cnd_fromCache);
            return new Response(cdn_responseBody.byteStream(), cnd_fromCache, cdn_responseBody.contentLength());
        }

        //如果web发来的不是图片的URL，直接用web返回的图片
        Log.i(TAG, "server response image source , Load image by web！！！");
        boolean fromCache = response.cacheResponse() != null;
        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

    @Override
    public void shutdown() {

        Cache cache = client.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
