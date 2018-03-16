package com.tamic.image.glidewarpper.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * 图片ImagePresenter
 * Created by Tamic on 2016-08-26.
 */
public class TcImagePresenter {

    private static final String TAG = "ImageWrapper";
    private Context context;
    private static TcImagePresenter sInstance;
    private ImageBridge imageBridge;

    /**
     * 获取单例对象
     *
     * @param aContext context
     * @return 返回 PaStaticsManager
     */
    public static synchronized TcImagePresenter getInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new TcImagePresenter(aContext, new ImageBridgeImpl(aContext));
        }
        return sInstance;
    }

    /**
     * constructor
     *
     * @param context context
     */
    private TcImagePresenter(Context context, ImageBridge imageBridge) {
        this.context = context;
        this.imageBridge = imageBridge;
    }


    public void init(Context context, float size, long caheSize) {

        imageBridge.init(context, size, caheSize);
    }

    public boolean isInitialized() {
        return imageBridge.isInitialized();
    }

    public void fetchImage(String filePathOrUrl) {
        imageBridge.fetchImage(filePathOrUrl);
    }

    public Bitmap getImage(String filePathOrUrl) throws IOException {
        return imageBridge.getImage(filePathOrUrl);
    }

    public void loadImage(final ImageView imageView, int resourceId, boolean isNeedCache, LoaderCallback callback) {
        imageBridge.loadImage(imageView, resourceId, isNeedCache, callback);
    }

    public void loadImage(final ImageView imageView, File file, int placeholderResId, LoaderCallback callback) {
        imageBridge.loadImage(imageView, file, placeholderResId, callback);

    }

    public void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                          LoaderCallback callback, boolean isNeedCache) {
        imageBridge.loadImage(imageView, filePathOrUrl, placeholderResId, callback, isNeedCache);
    }

    public void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId, int width, int height,
                          boolean isNeedCut, boolean isNeedCache, LoaderCallback callback) {
        imageBridge.loadImage(imageView, filePathOrUrl, placeholderResId, width, height, isNeedCut, isNeedCache, callback);

    }

    public void loadImage(final ImageView imageView, File file, int placeholderResId, int width,
                          int height, boolean isNeedCut, LoaderCallback callback) {
        imageBridge.loadImage(imageView, file, placeholderResId, width, height,
                isNeedCut, true, callback);
    }

    /*public void loadImage(final ImageView imageView, File file, int placeholderResId, int width,
                          int height, boolean isNeedCut, boolean isNeedCache, Callback callback) {
        loadImage(imageView, null, file, placeholderResId, width, height, isNeedCut, callback);
    }*/

    protected void cancelRequest(ImageView view) {
        imageBridge.cancelRequest(view);
    }

    protected void cancelRequest(CancelCallBack callBack) {
        imageBridge.cancelRequest(callBack);
    }

    public void clearCache(ImageView view) {
        imageBridge.clearMemoryCache(view);
    }


    protected void clearMemoryCache(String url, ImageView image) {
        imageBridge.clearMemoryCache(url, image);
    }

    @Deprecated
    public Bitmap removeMemoryCache(String key) {
        // TODO nothing 兼容之前，之后测试正常后删除
        return imageBridge.removeMemoryCache(key);
    }

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     */

    protected void clearCache(String path) {
        imageBridge.clearCache(null, null, path);
    }

    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     */
    protected void clearCache(Uri uri) {
        imageBridge.clearCache(uri);
    }

    protected void clearCache(File file) {
        imageBridge.clearCache(file);
    }


    protected void pauseTag(Object obj) {
        imageBridge.pauseTag(obj);
    }

    protected void resumeTag(Object obj) {
        imageBridge.resumeTag(obj);
    }

    protected void cancelTag(Object obj) {
        imageBridge.cancelTag(obj);
    }

    @Deprecated
    protected Map<String, Bitmap> snapshot() {
        return imageBridge.snapshot();
    }

    @Deprecated
    public void setMemCacheSize(int maxSize) {
        imageBridge.setMemCacheSize(maxSize);
    }

    @Deprecated
    protected void cancleTask() {
        imageBridge.cancleTask();
    }

    @Deprecated
    protected int getMemCacheSize() {
        return imageBridge.getMemCacheSize();
    }

}
