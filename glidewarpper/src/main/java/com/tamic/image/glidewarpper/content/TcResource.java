/*
 * Filename:    Resource.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2016
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-09-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * @gitHub: Tamic :https://github.com/Tamic
 */

package com.tamic.image.glidewarpper.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.tamic.image.glidewarpper.core.ImageWrapper;
import com.tamic.image.glidewarpper.util.TcReflectUtils;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 资源管理类
 * Created by Tamic on 2015/2/4
 */
@SuppressLint("NewApi")
public final class TcResource {

    /**
     * DEBUG mode
     */
    private static final boolean DEBUG = false;
    /**
     * DEBUG Build Mode
     */
    private static final boolean DEBUG_BUILD = false;
    /**
     * Log TAG
     */
    private static final String LOG_TAG = TcResource.class.getSimpleName();

    /**
     * Singleton Instance
     */
    private static TcResource sInstance;
    /**
     * 回收函数最大API Level
     */
    private Context mContext;
    /**
     * 确保OOM时仍然能够返回一张图
     */
    private Bitmap mOOMImage;
    /**
     * Bitmap缓存
     */
    private LongSparseArray<WeakReference<Bitmap>> mBitmapCache;
    /**
     * density
     */
    private static float sDensity;
    /**
     * 最大缓存个数
     */
    private static final int MAX_CACHE_SIZE = 100;

    /**
     * 缓存
     */
    private static HashMap<String, LruCache<String, Integer>> sResourceCache =
            new HashMap<String, LruCache<String, Integer>>();

    /**
     * Constructor
     */
    private TcResource() {
        mBitmapCache = new LongSparseArray<WeakReference<Bitmap>>();
    }

    /**
     * GetInstance
     *
     * @return TcResource
     */
    public static synchronized TcResource getInstance() {
        if (sInstance == null) {
            sInstance = new TcResource();
        }
        return sInstance;
    }

    /**
     * 载入相关资源
     *
     * @param aContext Context
     */
    public void load(Context aContext) {
        mOOMImage = getImage(aContext, TcResource.getResourceId("drawable", "a"));
    }

    /**
     * 获取Context，私有方法不对外开放
     *
     * @return Context
     */
    private Context getContext() {
        if (mContext == null) {
            mContext = ImageWrapper.getContext();
        }

        if (mContext == null) {
            throw new RuntimeException("PaImageLoder is init? >>>>>>>>> context is null!");
        }
        return mContext;
    }

    /**
     * Destroy
     */
    public void destroy() {
        mBitmapCache.clear();
        recycleBitmap(mOOMImage);
        mOOMImage = null;

        onDestroy();
    }

    /**
     * OnDestroy
     */
    private static void onDestroy() {
        // 该模块无需释放，支持两遍重入
        //sInstance = null;
    }

    /**
     * 获取图片
     *
     * @param aRes   Resource
     * @param aResId 资源句柄
     * @return Bitmap
     */
    public static Bitmap getImage(Resources aRes, int aResId) {
        try {
            //DrawableCache
            if (DEBUG_BUILD) {
                boolean cache = checkCachedDrawable(aRes, aResId);
                if (cache) {
                    String hexResId = Integer.toHexString(aResId);
                    Log.e(LOG_TAG, "***********************************************************");
                    Log.e(LOG_TAG, "****GetImage[0x" + hexResId + "] has cached in drawableCache!!!****");
                    Log.e(LOG_TAG, "***********************************************************");
                    Log.e(LOG_TAG, "FlyFlow has been stopped!!!", new RuntimeException());
                    Process.killProcess(Process.myPid());
                }
            }
            //BitmapCache
            WeakReference<Bitmap> wr = getInstance().mBitmapCache.get(aResId);
            if (wr != null) {
                Bitmap bitmap = wr.get();
                if (bitmap != null) {
                    return bitmap;
                } else {
                    // our bitmap has been purged
                    getInstance().mBitmapCache.delete(aResId);
                }
            }
            //Bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(aRes, aResId);
            if (bitmap != null) {
                return bitmap;
            }
        } catch (OutOfMemoryError e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage OutOfMemoryError", e);
            }
            System.gc();
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Error", e);
            }
        }
        return getInstance().mOOMImage;
    }

    /**
     * 获取图片
     *
     * @param aRes     Resource
     * @param aResId   资源句柄
     * @param aOptions Options
     * @return Bitmap
     */
    public static Bitmap getImage(Resources aRes, int aResId, Options aOptions) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(aRes, aResId, aOptions);
            if (bitmap != null) {
                return bitmap;
            }
        } catch (OutOfMemoryError e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage OutOfMemoryError", e);
            }
            System.gc();
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Error", e);
            }
        }
        return getInstance().mOOMImage;
    }

    /**
     * 获取图片
     *
     * @param aRes      Resource
     * @param aFilePath FilePath
     * @param aOptions  Options
     * @return Bitmap
     */
    public static Bitmap getImage(Resources aRes, String aFilePath, Options aOptions) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(aFilePath, aOptions);
            if (bitmap != null) {
                return bitmap;
            }
        } catch (OutOfMemoryError e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage OutOfMemoryError", e);
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getImage Error", e);
            }
        }
        return getInstance().mOOMImage;
    }

    /**
     * 获取图片
     *
     * @param aContext Context
     * @param aResId   资源句柄
     * @return Bitmap
     */
    public static Bitmap getImage(Context aContext, int aResId) {
        return getImage(aContext.getResources(), aResId);
    }

    /**
     * 获取图片
     *
     * @param aContext Context
     * @param aResId   资源句柄
     * @param aOptions Options
     * @return Bitmap
     */
    public static Bitmap getImage(Context aContext, int aResId, Options aOptions) {
        return getImage(aContext.getResources(), aResId, aOptions);
    }

    /**
     * 获取图片
     *
     * @param aContext  Context
     * @param aFilePath FilePath
     * @param aOptions  Options
     * @return Bitmap
     */
    public static Bitmap getImage(Context aContext, String aFilePath, Options aOptions) {
        return getImage(aContext.getResources(), aFilePath, aOptions);
    }


    /**
     * 回收图片
     *
     * @param aBitmap Bitmap
     */
    public static void recycleBitmap(Bitmap aBitmap) {
        if (aBitmap != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                aBitmap.recycle();
            }
        }
    }

    /**
     * 回收图片
     *
     * @param aResId 资源句柄
     */
    public static void recycleBitmap(int aResId) {
        WeakReference<Bitmap> wr = getInstance().mBitmapCache.get(aResId);
        if (wr != null) {
            Bitmap bitmap = wr.get();
            if (bitmap != null) {
                recycleBitmap(bitmap);
            }
            getInstance().mBitmapCache.delete(aResId);
        }
    }

    /**
     * 获取字符串
     *
     * @param aResId 资源句柄
     * @return 字符串
     */
    public static String getString(int aResId) {
        try {
            return getInstance().getContext().getResources().getString(aResId);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据name去获取字符串
     *
     * @param aResName 资源name
     * @return 字符串
     */
    public static String getString(String aResName) {
        return getInstance().getContext().getResources().getString(getResourceId("string", aResName));
    }

    /**
     * 获取Color
     *
     * @param aResId 资源句柄
     * @return RGB Color
     */
    public static int getColor(int aResId) {
        return getInstance().getContext().getResources().getColor(aResId);
    }

    /**
     * 根据name去获取色值
     *
     * @param aResName 资源name
     * @return color
     */
    public static int getColor(String aResName) {
        return getInstance().getContext().getResources().getColor(getResourceId("color", aResName));
    }

    /**
     * 根据name获取dimen
     *
     * @param aResName 资源name
     * @return dimen
     */
    public static float getDimension(String aResName) {
        return getInstance().getContext().getResources().getDimension(getResourceId("dimen", aResName));
    }

    /**
     * 根据name获取dimen
     *
     * @param aId 资源Id
     * @return dimen
     */
    public static float getDimension(int aId) {
        return getInstance().getContext().getResources().getDimension(aId);
    }

    /**
     * 根据name获取字符串数组
     *
     * @param aResName 资源name
     * @return 字符串数组
     */
    public static CharSequence[] getTextArray(String aResName) {
        return getInstance().getContext().getResources().getTextArray(getResourceId("array", aResName));
    }

    /**
     * 根据name获取字符串数组
     *
     * @param aId 资源id
     * @return 字符串数组
     */
    public static CharSequence[] getTextArray(int aId) {
        return getInstance().getContext().getResources().getTextArray(aId);
    }

    /**
     * openRawResource
     *
     * @param aId id
     * @return raw resource
     */
    public static InputStream openRawResource(int aId) {
        return getInstance().getContext().getResources().openRawResource(aId);
    }

    /**
     * 获取SharedPreferences
     *
     * @param aContext Context
     * @param aName    SharedPreferences Name
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreferences(Context aContext, String aName) {
        return aContext.getSharedPreferences(aName, Context.MODE_PRIVATE);
    }

    /**
     * 从XML中创建View
     *
     * @param aContext Context
     * @param aResId   Resource句柄
     * @param aRoot    父节点
     * @return View
     */
    public static View inflateLayout(Context aContext, int aResId, ViewGroup aRoot) {
        return LayoutInflater.from(aContext).inflate(aResId, aRoot);
    }

    /**
     * 检测DrawableCache是否存在该资源
     *
     * @param aRes   {@link Resources}
     * @param aResId 资源句柄
     * @return 如果存在则返回true，否则返回false
     */
    private static boolean checkCachedDrawable(Resources aRes, int aResId) {
        try {
            Drawable drawable = getCachedDrawable(aRes, aResId);
            if (drawable != null && drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap() != null;
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "checkCachedDrawable Exception", e);
            }
        }
        return false;
    }

    /**
     * 获取缓存资源
     *
     * @param aRes   {@link Resources}
     * @param aResId 资源句柄
     * @return 缓存资源
     */
    private static Drawable getCachedDrawable(Resources aRes, int aResId) {
        LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache = getDrawableCache(aRes, aResId);
        if (drawableCache != null) {
            final long key = getDrawableCacheKey(aRes, aResId);
            WeakReference<Drawable.ConstantState> wr = drawableCache.get(key);
            if (wr != null) {
                Drawable.ConstantState entry = wr.get();
                if (entry != null) {
                    return entry.newDrawable(aRes);
                }
            }
        }
        return null;
    }

    /**
     * 获取缓存资源对应的KEY
     *
     * @param aRes   {@link Resources}
     * @param aResId 资源句柄
     * @return 缓存资源对应的KEY
     */
    private static long getDrawableCacheKey(Resources aRes, int aResId) {
        TypedValue value = new TypedValue();
        aRes.getValue(aResId, value, true);
        return (((long) value.assetCookie) << 32) | value.data; // SUPPRESS CHECKSTYLE
    }

    /**
     * 获取缓存资源列表
     *
     * @param aRes   {@link Resources}
     * @param aResId 资源句柄
     * @return 缓存资源列表
     */
    @SuppressWarnings("unchecked")
    private static LongSparseArray<WeakReference<Drawable.ConstantState>> getDrawableCache(Resources aRes,
                                                                                           int aResId) {
        LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache = null;
        drawableCache = (LongSparseArray<WeakReference<Drawable.ConstantState>>) TcReflectUtils
                .getFieldValue(aRes, "mDrawableCache", drawableCache);
        return drawableCache;
    }

    /**
     * 获取资源id
     *
     * @param aResType 资源类型
     * @param aResName 资源name
     * @return id
     */
    public static int getResourceId(String aResType, String aResName) {
        LruCache<String, Integer> cache = sResourceCache.get(aResType);
        if (cache == null) {
            cache = new LruCache<String, Integer>(MAX_CACHE_SIZE);
            sResourceCache.put(aResType, cache);
        }
        Integer resId = cache.get(aResName);
        if (resId == null) {
            try {
                int bdResId = getInstance().getContext().getResources()
                        .getIdentifier(aResName, aResType, getInstance().getContext().getPackageName());
                cache.put(aResName, bdResId);
                return bdResId;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } catch (Error e) {
                e.printStackTrace();
                return 0;
            }
        }
//		PaLog.d(BdCore.TAG, "resource used=" + cache.toString());
        return resId.intValue();
    }

    /**
     * 获取Drawable
     *
     * @param aResourceId 对象ID
     * @return Drawable 对象
     */
    public static Drawable getDrawableById(int aResourceId) {
        try {
            return getInstance().getContext().getResources().getDrawable(aResourceId);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过name获得drawable
     *
     * @param aName resource name
     * @return drawable
     */
    public static Drawable getDrawableById(String aName) {
        int id = getResourceId("drawable", aName);
        return getDrawableById(id);
    }

    /**
     * @return density
     */
    public static float getDensity() {
        if (sDensity == 0) {
            sDensity = getInstance().getContext().getResources().getDisplayMetrics().density;
        }
        return sDensity;
    }
}
