package com.tamic.image.glidewarpper.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 图片加载框架桥接桥梁，为后面快速实现替换更新第三方框架而实现
 * Created by Tamic on 2016-08-25.
 */
public abstract class ImageBridge {


    abstract boolean isInitialized();

    abstract void init(Context context, float size, long caheSize);

    abstract <T> void loadImage(final ImageView imageView, T requestCreator,
                                int placeholderResId, boolean isNeedCache, LoaderCallback callback);

    abstract void loadImage(final ImageView imageView, int resourceId, boolean isNeedCache, LoaderCallback callback);


    public abstract void fetchImage(String filePathOrUrl);

    public abstract Bitmap getImage(String filePathOrUrl) throws IOException;


    /**
     * 通过文件路径或者Url加载图片
     *
     * @param imageView     Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl Start an image_load_err request using the specified path.
     */
    public void loadImage(final ImageView imageView, String filePathOrUrl) {
        loadImage(imageView, filePathOrUrl, 0);
    }

    /**
     * 通过文件路径或者Url加载图片，未加载完成前显示占位资源
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl    Start an image_load_err request using the specified path.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     */
    public void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId) {
        loadImage(imageView, filePathOrUrl, placeholderResId, null, true);
    }

    public void loadImage(final ImageView imageView, String filePathOrUrl, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, 0, callback, true);
    }

    /**
     * 通过文件路径或者Url加载图片，未加载完成前显示占位资源，占位资源不能为0.<br>
     * <br>
     * Note: The Callback param is a strong reference and will prevent your Activity or Fragment
     * from being garbage collected.
     * If you use this method, it is strongly recommended you invoke an adjacent P
     * icasso.cancelRequest(android.widget.ImageView) call to prevent temporary leaking.
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl    Start an image_load_err request using the specified path.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     * @param callback         by liz 加了一个 filePathOrUrl为空的判断
     */
    public abstract void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                   LoaderCallback callback, boolean isNeedCache);


    public void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                          int width, int height) {
        loadImage(imageView, filePathOrUrl, placeholderResId, width, height, false);
    }

    public void loadImage(final ImageView imageView, String filePathOrUrl,
                          int placeholderResId, int width, int height, boolean isNeedCut) {
        loadImage(imageView, filePathOrUrl, null, placeholderResId, width, height, isNeedCut, null);
    }

    /**
     * 通过文件加载图片
     *
     * @param imageView Asynchronously fulfills the request into the specified ImageView.
     * @param file      Start an image_load_err request using the specified image_load_err file.
     */
    public void loadImage(final ImageView imageView, File file) {
        loadImage(imageView, file, 0);
    }

    /**
     * 通过文件加载图片，未加载完成前显示占位资源
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param file             Start an image_load_err request using the specified image_load_err file.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     */
    public void loadImage(final ImageView imageView, File file, int placeholderResId) {
        loadImage(imageView, file, placeholderResId, null);
    }

    /**
     * 通过文件加载图片，未加载完成前显示占位资源，无占位资源id为0.<br>
     * <br>
     * Note: The Callback param is a strong reference and will prevent your Activity or Fragment
     * from being garbage collected.
     * If you use this method, it is strongly recommended you invoke an adjacent
     * Picasso.cancelRequest(android.widget.ImageView) call to prevent temporary leaking.
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param file             Start an image_load_err request using the specified image_load_err file.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     * @param callback
     */
    abstract void loadImage(final ImageView imageView, File file, int placeholderResId, LoaderCallback callback);

    /**
     * 通过资源文件加载图片
     *
     * @param imageView  Asynchronously fulfills the request into the specified ImageView.
     * @param resourceId Start an image_load_err request using the specified drawable resource ID.
     */
    public void loadImage(final ImageView imageView, int resourceId, boolean isNeedCache) {
        loadImage(imageView, resourceId, isNeedCache, null);
    }

    public void loadImage(final ImageView imageView, File file, int placeholderResId, int width,
                          int height, boolean isNeedCut, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, null, file, placeholderResId, width, height, isNeedCut, callback);
    }


    public abstract void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId, int width, int height,
                                   boolean isNeedCut, boolean isNeedCache, LoaderCallback callback);

    public abstract void loadImage(final ImageView imageView, String filePathOrUrl, File file, int placeholderResId,
                                   int width, int height, boolean isNeedCut, LoaderCallback callback);

    abstract <T> void loadImage(final ImageView imageView, T requestCreator, int placeholderResId,
                                int width, int height, boolean isNeedCut, LoaderCallback callback);

   /* abstract void loadImage(final ImageView imageView, DrawableTypeRequest requestCreator, int placeholderResId,
                            int width, int height, boolean isNeedCut, LoaderCallback callback);*/

    /**
     * cancelRequest
     *
     * @param image
     */
    abstract void cancelRequest(ImageView image);

    protected void clearMemoryCache(ImageView image) {
        clearMemoryCache(null, image);
    }

    abstract void clearMemoryCache(String url, ImageView image);

    abstract void cancelRequest(CancelCallBack cancelCallBack);

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     * {@linkplain RequestCreator#stableKey stable key}.
     */

    protected void clearCache(String path) {
        clearCache(null, null, path);
    }


    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     */
    protected void clearCache(Uri uri) {
        clearCache(uri, null, null);

    }

    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     */
    protected void clearCache(File file) {
        clearCache(null, file, null);
    }

    abstract void clearCache(Uri uri, File file, String path);


    abstract void pauseTag(Object obj);

    abstract void resumeTag(Object obj);

    abstract void cancelTag(Object obj);

    @Deprecated
    public Bitmap removeMemoryCache(String key) {
        // TODO nothing 兼容之前，之后测试正常后删除
        // key 只能以文件路径或url为主 其他自定义无发兼容以前版本
        clearCache(key);
        return null;
    }

    @Deprecated
    public abstract Map<String, Bitmap> snapshot();

    @Deprecated
    public abstract void setMemCacheSize(int maxSize);

    @Deprecated
    public abstract void cancleTask();

    @Deprecated
    public abstract int getMemCacheSize();


}
