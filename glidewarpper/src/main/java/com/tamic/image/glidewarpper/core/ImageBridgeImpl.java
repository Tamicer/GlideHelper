package com.tamic.image.glidewarpper.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.pinganfang.imagelibrary.R;
import com.pinganfang.imagelibrary.cache.CaheInterceptor;
import com.pinganfang.imagelibrary.cache.ImageCacheManager;
import com.pinganfang.imagelibrary.util.NullUtils;
import com.pinganfang.imagelibrary.widget.PaImageView;
import com.pinganfang.imagelibrary.picasso.Callback;
import com.pinganfang.imagelibrary.picasso.MemoryPolicy;
import com.pinganfang.imagelibrary.picasso.NetworkPolicy;
import com.pinganfang.imagelibrary.picasso.Picasso;
import com.pinganfang.imagelibrary.picasso.RequestCreator;
import com.pinganfang.imagelibrary.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * 框架集成具体实现类，抽离业务和具体实现
 * Created by LIUYONGKUI726 on 2016-08-25.
 */
public class ImageBridgeImpl extends ImageBridge {

    private static final String TAG = "PaImageLoaderImpl";
    private Context context;
    private Picasso mPicasso;
    private OkHttpClient client;
    private Map<String, ImageView> caches = new HashMap<>();
    private long maxSize;
    private ImageCacheManager cacheManager;
    private Bitmap.Config defConfig = Bitmap.Config.RGB_565;


    public ImageBridgeImpl(Context context) {
        this.context = context;
    }


    @Override
    boolean isInitialized() {
        return !NullUtils.isNull(mPicasso);
    }

    @Override
    void init(Context context, float size, long maxSize) {
        this.maxSize = maxSize;
        // New cacheManager
        cacheManager = ImageCacheImpl();
        cacheManager.setMaxSize(maxSize);
        // Generate the global default Picasso instance.
        mPicasso = getPicasso(context, null);
        mPicasso.setLoggingEnabled(true);
        //Picasso.setSingletonInstance(mPicasso);

    }

    /**
     * Not singleton
     */
    private OkHttpClient getProgressClient(final ImageResponseBody.ProgressListener listener) {

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new OkHttpClient
                .Builder()
                .sslSocketFactory(sc.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cache(new Cache(cacheManager.cache(), cacheManager.getCacheMaxSize()))
//                .addInterceptor(new CaheInterceptor(context, listener))
                .addNetworkInterceptor(new CaheInterceptor(context, listener))
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();

    }


    /**
     * Download Big Image only, singleton but  shared cache
     */
    public Picasso getPicasso(Context context, ImageResponseBody.ProgressListener listener) {
        client = getProgressClient(listener);
        return mPicasso == null ? mPicasso = new Picasso.Builder(context)
                .downloader(new ImageDownLoader(client))
                .build() : mPicasso;
    }

    @Override
    <T> void loadImage(ImageView imageView, T requestCreator, int placeholderResId, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, null, null, (RequestCreator) requestCreator, placeholderResId, 0, 0, false, isNeedCache, callback);
    }

    @Override
    void loadImage(ImageView imageView, int resourceId, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, null, resourceId, isNeedCache, callback);
    }

    @Override
    public void fetchImage(String filePathOrUrl) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, you must init PaImageloder: call PaImageloder.inti()");
        mPicasso.load(filePathOrUrl).fetch();
    }

    @Override
    public Bitmap getImage(String filePathOrUrl) throws IOException {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, you must init PaImageloder: call PaImageloder.inti()");
        return mPicasso.load(filePathOrUrl).get();
    }

    @Override
    public void loadImage(ImageView imageView, String filePathOrUrl, int placeholderResId,
                          LoaderCallback callback, boolean isNeedCache) {
        imageView = NullUtils.checkNotNull(imageView, "ImageView == Null, Are you init ImageView?");
        if (null == filePathOrUrl || TextUtils.isEmpty(filePathOrUrl.trim())) {
            loadImage(imageView, placeholderResId, isNeedCache, callback);
        } else {
            loadImage(imageView, getRequestCreator(filePathOrUrl), placeholderResId, isNeedCache, callback);
        }
    }

    @Override
    void loadImage(ImageView imageView, File file, int placeholderResId, LoaderCallback callback) {

        if ((NullUtils.isNull(file) || !file.exists()) && placeholderResId == 0) {
            Log.e(TAG, "File is null or not exists, please check file!");
            return;
        }
        if (placeholderResId != 0) {
            Log.v(TAG, "===== File is null or not exists, load place holder resource. =====");
            loadImage(imageView, placeholderResId, true, callback);
        } else {
            Log.v(TAG, "===== Load image_load_err file path : " + file.getPath() + " =====");
            NullUtils.checkNotNull(mPicasso, "Picasso == Null, you must init PaImageloder: call PaImageloder.inti()");
            loadImage(imageView, mPicasso.load(file), placeholderResId, true, callback);
        }
    }

    @Override
    public void loadImage(ImageView imageView, String filePathOrUrl, int placeholderResId, int width,
                          int height, boolean isNeedCut, boolean isNeedCache, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, null, null, placeholderResId, width,
                height, isNeedCut, isNeedCache, callback);
    }

    @Override
    public void loadImage(ImageView imageView, String filePathOrUrl, File file, int placeholderResId,
                          int width, int height, boolean isNeedCut, LoaderCallback callback) {
        loadImage(imageView, filePathOrUrl, file, null, placeholderResId, width, height, isNeedCut, true, callback);

    }

    @Override
    <T> void loadImage(ImageView imageView, T requestCreator, int placeholderResId, int width, int height,
                       boolean isNeedCut, LoaderCallback callback) {
        requestCreator = NullUtils.checkNotNull(requestCreator, "requestCreator == Null, Are you init requestCreator?");
        loadImage(imageView, null, null, (RequestCreator) requestCreator, placeholderResId, width, height, false, isNeedCut, callback);
    }

    /**
     * loadImage Core 勿改
     *
     * @param imageView        　　目标ｖｉｅｗ
     * @param filePathOrUrl    路径或ＵＲＬ
     * @param file
     * @param requestCreator   　Ｐｉｃａｓｓｏ包装requestCreator对象
     * @param placeholderResId 占位符
     * @param width
     * @param height
     * @param isNeedCut        是否裁剪
     * @param isNeedCache      是否缓存
     * @param callback         回调
     */
    public void loadImage(ImageView imageView, final String filePathOrUrl, File file, RequestCreator
            requestCreator, int placeholderResId, int width, int height, boolean isNeedCut, boolean isNeedCache, final LoaderCallback callback) {
        imageView = NullUtils.checkNotNull(imageView, "ImageView == Null, you must init ImageView?");
        if (imageView instanceof PaImageView) {
            Log.v(TAG, "load ：>>> imageView == PaImageView");
            PaImageView processImage = (PaImageView) imageView;
            if (!NullUtils.isNull(filePathOrUrl)) {
                processImage.setTag(filePathOrUrl);
            }
            mPicasso = getPicasso(context, processImage);
            mPicasso.setLoggingEnabled(true);
        }

        RequestCreator creator = null;
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (TextUtils.isEmpty(filePathOrUrl) && file == null && requestCreator == null) {

            Log.v(TAG, "===== url is null or not available, load from default resource ===");
            if (placeholderResId != 0) {
                 creator = mPicasso.load(placeholderResId);
            }
        }

        if (requestCreator != null) {
            creator = requestCreator;
        }

        if (NullUtils.isNull(file) && !TextUtils.isEmpty(filePathOrUrl) && requestCreator == null) {
            creator = getRequestCreator(filePathOrUrl);
            caches.put(filePathOrUrl.trim(), imageView);
        }

        if (file != null) {
            creator = mPicasso.load(file);
        }

        if (creator == null) {
            creator = mPicasso.load(placeholderResId);
        }

        if (placeholderResId != 0) {
            creator.placeholder(placeholderResId);
        } else {
            creator.placeholder(R.drawable.lib_default_img_big);
        }

        //creator.error(R.drawable.image_load_err);

        TcTransformation transformation = new TcTransformation(0, 0);
        if (isNeedCut) {
            if (width > 0 && height > 0) {
                // Bug 勿用
                //creator.resize(width, height);
                transformation = new TcTransformation(width, height);
            }
        } else {
            transformation = new TcTransformation(0, 0);
            //creator.fit().centerInside();
        }
        creator.transform(transformation);

        if (!isNeedCache) {
            creator = skipMemoryCache(creator);
        } /*else {
           // creator = creator.networkPolicy(NetworkPolicy.OFFLINE);
        }*/
        creator = cutDownMemory(creator);

        if (callback == null) {
            creator.into(imageView);
            return;
        }

        final TcTransformation finalTransformation = transformation;
        creator.into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(finalTransformation.key(), filePathOrUrl, finalTransformation.getBitmap());
            }

            @Override
            public void onError() {

                callback.onError();

            }
        });
    }


    @Override
    void cancelRequest(ImageView imageView) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        imageView = NullUtils.checkNotNull(imageView, "ImageView == Null, Are you init ImageView?");
        mPicasso.cancelRequest(imageView);

    }

    @Override
    void pauseTag(Object obj) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (NullUtils.isNull(obj)) {
            return;
        }
        mPicasso.pauseTag(obj);

    }

    @Override
    void resumeTag(Object obj) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (NullUtils.isNull(obj)) {
            return;
        }
        mPicasso.resumeTag(obj);
    }

    @Override
    void cancelTag(Object obj) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (NullUtils.isNull(obj)) {
            return;
        }
        mPicasso.cancelTag(obj);
    }

    @Override
    public Map<String, Bitmap> snapshot() {
        return null;
    }

    @Override
    public void setMemCacheSize(int maxSize) {
        NullUtils.checkNotNull(cacheManager, "You must be init imageLoader at Applcation!");
        cacheManager.setMaxSize(maxSize);
    }

    @Override
    public void cancleTask() {

    }

    @Override
    public int getMemCacheSize() {
        return (int) cacheManager.getCacheSize();
    }


    @Override
    void clearMemoryCache(String url, ImageView view) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (TextUtils.isEmpty(url)) {
            clearCache(view);
        }
        skipMemoryCache(cutDownMemory(mPicasso.load(url))).into(view);
    }

    /**
     * learCache
     *
     * @param image
     */
    protected void clearCache(ImageView image) {

        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        image = NullUtils.checkNotNull(image, "ImageView == Null, Are you init ImageView?");
        if (!caches.containsValue(image)) {
            return;
        }
        if (caches != null && caches.size() > 0) {
            return;
        }
        Set<String> keys = caches.keySet();
        for (String cacheKey : keys) {
            if (caches.get(cacheKey) == image) {
                mPicasso.invalidate(Uri.parse(cacheKey));
                caches.remove(cacheKey);
                return;
            }
        }
    }

    @Override void clearCache(Uri uri, File file, String path) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        if (!TextUtils.isEmpty(uri.toString())) {
            mPicasso.invalidate(uri);
            return;
        }
        if (!NullUtils.isNull(file)) {
            mPicasso.invalidate(file);
            return;
        }
        if (!TextUtils.isEmpty(path)) {
            mPicasso.invalidate(path);
        }
    }

    /**
     * cancelRequest by Taret
     *
     * @param cancelCallBack
     */
    @Override void cancelRequest(CancelCallBack cancelCallBack) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        cancelCallBack = NullUtils.checkNotNull(cancelCallBack, "CancelCallBack == Null, Are you init Target?");
        final CancelCallBack finalCancelCallBack = cancelCallBack;
        mPicasso.cancelRequest(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom afrom) {
                finalCancelCallBack.onBitmapLoaded(bitmap, LoadedFrom.valueOf(afrom.name()));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                finalCancelCallBack.onBitmapFailed(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                finalCancelCallBack.onPrepareLoad(placeHolderDrawable);
            }
        });
    }

    /**
     * getRequestCreator
     *
     * @param filePathOrUrl
     * @return
     */
    public RequestCreator getRequestCreator(String filePathOrUrl) {
        NullUtils.checkNotNull(mPicasso, "Picasso == Null, Are you  init PaImageloder?: call PaImageloder.init()");
        File file = new File(filePathOrUrl);
        if (file != null && file.exists()) {
            return mPicasso.load(Uri.fromFile(file));
        }
        return mPicasso.load(filePathOrUrl);
    }

    public RequestCreator skipMemoryCache(RequestCreator requestCreator) {
        return requestCreator.memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE);
    }

    public RequestCreator cutDownMemory(RequestCreator requestCreator) {
        return requestCreator.config(defConfig);
    }

    private ImageCacheManager ImageCacheImpl() {


        return new ImageCacheManager(context) {

            @Override
            public String cacheDir() {
                return null;
            }

            @Override
            public String cacheName() {
                return null;
            }


        };
    }

}
