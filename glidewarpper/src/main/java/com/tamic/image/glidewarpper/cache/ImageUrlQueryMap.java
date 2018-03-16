package com.tamic.image.glidewarpper.cache;

import android.net.Uri;

import java.util.HashMap;

/**
 * Created by Tamic on 2017/9/7.
 */

public class ImageUrlQueryMap {

    private static HashMap<String, String> urlQueryMap = new HashMap<>();

    public static Uri saveQuery(Uri uri) {
        String query = uri.getQuery();
        if (query != null && query.contains("Tamic")) {
            String shortUrl = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
            urlQueryMap.put(shortUrl, query);
            return Uri.parse(shortUrl);
        } else {
            return uri;
        }
    }

    public static String getQuery(String shortUri) {
        return urlQueryMap.get(shortUri);
    }

    public static void removeQuery(String shortUri) {
        urlQueryMap.remove(shortUri);
    }
}
