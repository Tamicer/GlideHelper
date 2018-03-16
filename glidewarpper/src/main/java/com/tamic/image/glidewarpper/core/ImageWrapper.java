/*
 * Filename:    ImageLoader.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2016
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-08-25
 *
 * Modification History:
 * Date         Author      Version     Description
 * 2016.9.1     Tamic   1.0
 * ------------------------------------------------------------------
 * Created by Tamic on 2016-08-25.
 *
 */

package com.tamic.image.glidewarpper.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.ImageView;


import com.tamic.image.glidewarpper.cache.ImageCacheManager;
import com.tamic.image.glidewarpper.compressor.ImageCompressorPresenter;
import com.tamic.image.glidewarpper.compressor.OnCompressListener;
import com.tamic.image.glidewarpper.util.NullUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 图片基础加载库和处理库
 * <p/>
 * Created by Tamic on 2016-08-25.
 */
public class ImageWrapper {

    private static Context mContext;

    private ImageWrapper() {
    }


    public static boolean isInitialized(Context context) {
        return TcImagePresenter.getInstance(context).isInitialized();
    }

    /**
     * init imaglaoder
     * init
     *
     * @param context
     */
    public static void initialize(Context context) {
        initialize(context, 0.1f);
    }

    /**
     * init imaglaoder
     *
     * @param context
     * @param size
     */
    public static void initialize(Context context, float size) {
        initialize(context, size, ImageCacheManager.maxSize);
    }

    /**
     * init imaglaoder
     *
     * @param context
     * @param size
     */
    public static void initialize(Context context, float size, long maxSize) {
        mContext = context;
        TcImagePresenter.getInstance(context).init(context, size, maxSize);
    }

    public static Context getContext() {
        return mContext;
    }


    /**
     * 预加载
     * 不会展现到view上，只异步去离线好需要图片
     *
     * @param url
     */
    public static void preLoadImage(String url) {
        TcImagePresenter.getInstance(mContext).fetchImage(url);
    }

    /**
     * 预加载并返回需要的 Bitmap
     * 不会展现到view上，离线好需要图片 并返回对应的Bitmap资源
     * 请在非UI线程使用
     *
     * @param filePathOrUrl
     */
    public static Bitmap getLoadImage(String filePathOrUrl) throws IOException {
        return TcImagePresenter.getInstance(mContext).getImage(filePathOrUrl);
    }

    /**
     * 通过文件路径或者Url加载图片
     *
     * @param imageView     Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl Start an image_load_err request using the specified path.
     */
    public static void loadImage(final ImageView imageView, String filePathOrUrl) {
        loadImage(imageView, filePathOrUrl, true);
    }

    /**
     * 通过文件路径或者Url加载图片
     *
     * @param imageView     Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl Start an image_load_err request using the specified path.
     */
    public static void loadImage(final ImageView imageView, String filePathOrUrl, boolean isNeedCache) {
        loadImage(imageView, filePathOrUrl, 0, isNeedCache);
    }

    /**
     * 通过文件路径或者Url加载图片，未加载完成前显示占位资源
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl    Start an image_load_err request using the specified path.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     */
    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId) {
        loadImage(imageView, filePathOrUrl, placeholderResId, true);
    }

    /**
     * 通过文件路径或者Url加载图片，未加载完成前显示占位资源
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param filePathOrUrl    Start an image_load_err request using the specified path.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     */
    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId, boolean isNeedCache) {
        loadImage(imageView, filePathOrUrl, placeholderResId, null, isNeedCache);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, true, callback);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, 0, callback, isNeedCache);
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
    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                 LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, placeholderResId, callback, true);

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
    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                 LoaderCallback callback, boolean isNeedCache) {
        TcImagePresenter.getInstance(mContext).loadImage(imageView, filePathOrUrl, placeholderResId, callback, isNeedCache);

    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                 int width, int height) {
        loadImage(imageView, filePathOrUrl, placeholderResId, width, height, false, true);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl,
                                 int placeholderResId, int width, int height, boolean isNeedCut) {
        loadImage(imageView, filePathOrUrl, placeholderResId, width, height, isNeedCut, true);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl,
                                 int width, int height, boolean isNeedCut, boolean isNeedCache) {
        loadImage(imageView, filePathOrUrl, 0, width, height, isNeedCut, isNeedCache, null);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl,
                                 int placeholderResId, int width, int height, boolean isNeedCut, boolean isNeedCache) {
        loadImage(imageView, filePathOrUrl, placeholderResId, width, height, isNeedCut, isNeedCache, null);
    }

   /* *//**
     * 通过文件加载图片
     *  @param imageView Asynchronously fulfills the request into the specified ImageView.
     * @param file      Start an image_load_err request using the specified image_load_err file.
     *//*
    public static void loadImage(final ImageView imageView, String file) {
        loadImage(imageView, file, 0);
    }*/

    /**
     * 通过文件加载图片，未加载完成前显示占位资源
     *
     * @param imageView        Asynchronously fulfills the request into the specified ImageView.
     * @param file             Start an image_load_err request using the specified image_load_err file.
     * @param placeholderResId A placeholder drawable to be used while the image_load_err is being loaded.
     */
    public static void loadImage(final ImageView imageView, File file, int placeholderResId) {
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
    public static void loadImage(final ImageView imageView, File file, int placeholderResId, LoaderCallback callback) {

        TcImagePresenter.getInstance(mContext).loadImage(imageView, file, placeholderResId, callback);
    }

    /**
     * 通过资源文件加载图片
     *
     * @param imageView  Asynchronously fulfills the request into the specified ImageView.
     * @param resourceId Start an image_load_err request using the specified drawable resource ID.
     */
    public static void loadImage(final ImageView imageView, int resourceId) {
        loadImage(imageView, resourceId, null);
    }

    /**
     * 通过资源文件加载图片，未加载完成前显示占位资源，无占位资源id为0.<br>
     * <br>
     * Note: The Callback param is a strong reference and will prevent your Activity
     * or Fragment from being garbage collected.
     * If you use this method, it is strongly recommended you invoke an adjacent
     * Picasso.cancelRequest(android.widget.ImageView) call to prevent temporary leaking.
     *
     * @param imageView  Asynchronously fulfills the request into the specified ImageView.
     * @param resourceId Start an image_load_err request using the specified drawable resource ID.
     * @param callback
     */
    public static void loadImage(final ImageView imageView, int resourceId, LoaderCallback callback) {
        loadImage(imageView, resourceId, true, callback);
    }

    /**
     * 通过资源文件加载图片，未加载完成前显示占位资源，无占位资源id为0.<br>
     * <br>
     * Note: The Callback param is a strong reference and will prevent your Activity
     * or Fragment from being garbage collected.
     * If you use this method, it is strongly recommended you invoke an adjacent
     * Picasso.cancelRequest(android.widget.ImageView) call to prevent temporary leaking.
     *
     * @param imageView  Asynchronously fulfills the request into the specified ImageView.
     * @param resourceId Start an image_load_err request using the specified drawable resource ID.
     * @param callback
     */
    public static void loadImage(final ImageView imageView, int resourceId, boolean isNeedCache, LoaderCallback callback) {
        TcImagePresenter.getInstance(mContext).loadImage(imageView, resourceId, isNeedCache, callback);
    }

    public static void loadImage(final ImageView imageView, File file, int placeholderResId, int width,
                                 int height, boolean isNeedCut, LoaderCallback callback) {
        TcImagePresenter.getInstance(mContext).loadImage(imageView, file, placeholderResId, width, height,
                isNeedCut, callback);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl,
                                 int width, int height, boolean isNeedCut, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, 0,
                width, height, isNeedCut, isNeedCache, callback);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                 int width, int height, boolean isNeedCut, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, placeholderResId,
                width, height, isNeedCut, true, callback);
    }

    public static void loadImage(final ImageView imageView, String filePathOrUrl, int placeholderResId,
                                 int width, int height, boolean isNeedCut, boolean isNeedCache, LoaderCallback callback) {
        TcImagePresenter.getInstance(mContext).loadImage(imageView, filePathOrUrl, placeholderResId,
                width, height, isNeedCut, isNeedCache, callback);
    }

    /**
     * 暂停加载
     * <p/>
     * 可在listview滑动时停止加载，停止滑动是恢复加载
     * 和resumeLoader（）配合使用
     * Pause existing requests with the given tag. Use {@link # ImageBridgeImpl.resumeTag(Object)}
     * to resume requests with the given tag.
     *
     * @see #pauseLoader(Object)
     * @see ImageBridgeImpl #tag(Object)
     */
    public static void pauseLoader(Object obj) {
        TcImagePresenter.getInstance(mContext).pauseTag(obj);
    }

    /**
     * 恢复加载
     * 可在listview滑动时停止加载，停止滑动是恢复加载
     * 和 pauseLoader（）配合使用
     * <p/>
     * Resume paused requests with the given tag. Use {@link #pauseLoader(Object)}
     * to pause requests with the given tag.
     * <p/>
     * ex:
     * PaImageLoade.resumeLoader("this is you_tag")
     *
     * @see #pauseLoader(Object)
     * @see @see ImageBridgeImpl #tag(Object)
     */
    public static void resumeLoader(Object obj) {
        TcImagePresenter.getInstance(mContext).resumeTag(obj);
    }

    /**
     * 取消加载
     * ex:
     * PaImageLoade.cancelLoader("this is you_tag")
     *
     * @param obj
     */
    public static void cancelLoader(Object obj) {
        TcImagePresenter.getInstance(mContext).cancelTag(obj);
    }

    /**
     * 取消请求
     * cancelRequest
     * Cancel any existing requests for the specified target {@link ImageView}.
     *
     * @param view
     */
    public static void cancelRequest(ImageView view) {
        TcImagePresenter.getInstance(mContext).cancelRequest(view);
    }

    /**
     * 取消请求
     *
     * @param callBack
     */
    public static void cancelRequest(CancelCallBack callBack) {
        TcImagePresenter.getInstance(mContext).cancelRequest(callBack);
    }

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     * {@linkplain ImageBridgeImpl# clearCache stable key}.
     */
    public static void clearCache(String path) {
        TcImagePresenter.getInstance(mContext).clearCache(path);
    }

    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     */
    public static void clearCache(Uri uri) {
        TcImagePresenter.getInstance(mContext).clearCache(uri);
    }

    /**
     * Invalidate all memory cached images for the specified {@code file}.
     */
    public static void clearCache(File file) {
        TcImagePresenter.getInstance(mContext).clearCache(file);
    }

    @Deprecated
    public static void clearMemoryCache(ImageView view) {
        // TODO nothing 兼容之前，之后测试正常后删除
        if (NullUtils.isNull(view)) {
            return;
        }
        TcImagePresenter.getInstance(mContext).clearCache(view);
    }


    /**
     * 推荐
     *
     * @param url
     * @param view
     */
    public static void clearMemoryCache(String url, ImageView view) {
        TcImagePresenter.getInstance(mContext).clearMemoryCache(url, view);
    }

    @Deprecated
    public static void clearMemoryCache() {
        // TODO nothing 兼容之前，之后测试正常后删除
        clearMemoryCache(null);
    }

    /**
     * / key只能以文件路径或url为主 其他自定义无发兼容以前版本
     *
     * @param key
     * @return
     */
    @Deprecated
    public static Bitmap removeMemoryCache(String key) {
        // TODO nothing 兼容之前，之后测试正常后删除
        return TcImagePresenter.getInstance(mContext).removeMemoryCache(key);
    }

    @Deprecated
    public static Map<String, Bitmap> snapshot() {
        // TODO nothing 兼容之前，之后测试正常后删除

        return TcImagePresenter.getInstance(mContext).snapshot();
    }

    @Deprecated
    public static void setMemCacheSize(int maxSize) {
        // TODO nothing 兼容之前，之后测试正常后删除
        TcImagePresenter.getInstance(mContext).setMemCacheSize(maxSize);
    }

    @Deprecated
    public static void cancleTask() {
        // TODO nothing 兼容之前，之后测试正常后删除
        TcImagePresenter.getInstance(mContext).cancleTask();
    }

    @Deprecated
    public static int getMemCacheSize() {
        // TODO nothing 兼容之前，之后测试正常后删除
        return TcImagePresenter.getInstance(mContext).getMemCacheSize();
    }

    public static Bitmap rotate(Bitmap bitmap, int angle) {
        return ImageCompressorPresenter.rotate(bitmap, angle);
    }

    public static Bitmap rotate(String imagePath, int angle) {
        return ImageCompressorPresenter.rotate(imagePath, angle);
    }

    public static Bitmap scale(Bitmap src, float scale) {
        return ImageCompressorPresenter.scale(src, scale);
    }

    public static Bitmap scale(String imagePath, float scale) {
        return ImageCompressorPresenter.scale(imagePath, scale);
    }

    public static Bitmap scale(Bitmap src, float scaleX, float scaleY) {
        return ImageCompressorPresenter.scale(src, scaleX, scaleY);
    }

    public static Bitmap scale(String imagePath, float scaleX, float scaleY) {
        return ImageCompressorPresenter.scale(imagePath, scaleX, scaleY);
    }

    public static Bitmap scale(Bitmap src, Matrix scaleMatrix) {
        return ImageCompressorPresenter.scale(src, scaleMatrix);
    }

    public static Bitmap scale(String imagePath, Matrix scaleMatrix) {
        return ImageCompressorPresenter.scale(imagePath, scaleMatrix);
    }

    public static Bitmap scale(String imagePath, int width, int height) {
        return ImageCompressorPresenter.scale(imagePath, width, height);
    }

    public static Bitmap scale(Bitmap bitmap, int width, int height) {
        return ImageCompressorPresenter.scale(bitmap, width, height);
    }

    public static Bitmap zoom(Bitmap bitmap, int w, int h) {
        return ImageCompressorPresenter.zoom(bitmap, w, h);
    }

    public static Bitmap zoom(String imagePath, int w, int h) {
        return ImageCompressorPresenter.zoom(imagePath, w, h);
    }

    public static void clipImage(Activity activity, int type, Uri data, int requestCode) {
        ImageCompressorPresenter.clipImage(activity, type, data, requestCode);
    }

    public static void compressor(Context context, int gear, File actualImage, OnCompressListener onCompressListener) {
        if (gear != ImageCompressorPresenter.Luban.THIRD_GEAR || gear != ImageCompressorPresenter.Luban.FIRST_GEAR) {
            gear = ImageCompressorPresenter.Luban.THIRD_GEAR;
        }
        ImageCompressorPresenter.Luban.get(context)
                .load(actualImage)
                .putGear(gear)
                .setCompressListener(onCompressListener).launch();
    }

    public static void compressor(Context context, File actualImage, OnCompressListener onCompressListener) {
        compressor(context, ImageCompressorPresenter.Luban.THIRD_GEAR, actualImage, onCompressListener);
    }

}
