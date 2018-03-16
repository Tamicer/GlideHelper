/*
 * Filename:    ImageCacheManager.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2016
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-09-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * Created by Tamic on 2016-08-31.
 */

package com.tamic.image.glidewarpper.cache;

import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;

/**
 * 图片缓存管理者
 * Created by Tamic on 2016-08-31.
 */
public abstract class ImageCacheManager {

    private Context context;
    private File imageCache;
    private String cacheDir;
    private String cacheName = getContext().getPackageName() + "_image_cache";
    public static long maxSize = 1024 * 1024 * 100;// 1G
    private static final int minSize = 50 * 1024 * 1024; // 50MB
    public static final int ONLINE_MAXS_TALE = 60 * 50; // 5min
    public static final int OFFE_LINE_MAX_STALE = 60 * 60 * 24 * 7;// 7 day

    public ImageCacheManager(Context context) {
        this.context = context;
        if (imageCache == null) {
            if (!TextUtils.isEmpty(cacheDir())) {
                setCacheDir(cacheDir());
            } else {
                cacheDir = context.getApplicationContext().getCacheDir().getAbsolutePath() + File.separator + cacheName;
                Log.d(CaheInterceptor.TAG, "Image cacheDir: " + cacheDir);
            }
            imageCache = createDefaultCacheDir();
        }
    }

    private File createDefaultCacheDir() {
        File cache = new File(cacheDir, cacheName);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private static long calculateDiskCacheSize(File dir) {
        long size = maxSize;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, maxSize), minSize);

    }

    public File cache() {
        return imageCache;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public abstract String cacheDir();

    public String getCacheName() {
        return cacheName;
    }

    public long getCacheSize() {
        return imageCache.length();
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public abstract String cacheName();

    public long getCacheMaxSize() {
        return calculateDiskCacheSize(imageCache);
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
