/*
 * Filename:    AutoImageView.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2016
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-09-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * Created by Tamic on 2016-09-01.
 */

package com.tamic.image.glidewarpper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.tamic.image.glidewarpper.R;
import com.tamic.image.glidewarpper.content.TcResource;
import com.tamic.image.glidewarpper.content.TcRoundedDrawable;
import com.tamic.image.glidewarpper.core.IImageLoaderHandler;
import com.tamic.image.glidewarpper.core.ImageWrapper;
import com.tamic.image.glidewarpper.core.LoaderCallback;


/**
 * 图片控件类，封装联网获取图片功能
 * 内部集成ImageLoder,无需再次加载，直接srtUrl()即可实现加载网络图片功能
 *
 * @author Tamic
 */
@SuppressLint("AppCompatCustomView")
public class TcAutoImageView extends ImageView implements IImageLoaderHandler {

    /**
     * TAG
     */
    private static final String TAG = "PaLoadingImageView";
    /**
     * 渐变显示动画的持续时间
     */
    private static final int DURATION_ANIM_FADE_IN = 500;
    /**
     * 最大角度
     */
    private static final int ANGLE_MAX = 360;
    /**
     * 默认图片，使用static减少对象数
     */
    private static Bitmap sDefaultBitmap;
    /**
     * 默认时间曲线
     */
    private static AccelerateInterpolator mDefaultInterpolator;
    /**
     * 图片地址
     */
    private String mImgUrl;
    /**
     * Key
     */
    private String mImgKey;
    /**
     * 加载完成后的渐变显示动画
     */
    private Animation mFadeInAnimation;
    /**
     * 开启加载图片渐变效果
     */
    private boolean mEnableFadeIn;
    /**
     * 开始渐现动画的起始alpha
     */
    private float mStartAlpha;
    /**
     * 是否将ImageView显示为圆形,需要将宽高设置为相等
     */
    private boolean mEnableCircle;
    /**
     * 圆角半径
     */
    private int mRadius = -1;
    /**
     * 使用圆角时离边距的距离，默认为0
     */
    private int mRadiusMarin;
    /**
     * Paint
     */
    private Paint mMarginPaint;
    /**
     * 边框线的宽度
     */
    private float mMarginWidth;
    /**
     * 启用边框颜色
     */
    private boolean mEnableMarginColor;
    /**
     * RectF
     */
    private RectF mRectF = new RectF();

    /**
     * 自定义实现,用于实现当异步加载完成Bitmap时回调此接口,可同构此接口实现ImageView不支持的功能
     */
    private LoaderCallback mLoaderCallback;
    /**
     * 是否需要强制缩放到父View宽度
     */
    private boolean mNeedFullWidth;
    /**
     * 是否需要默认图片
     */
    private boolean mNeedDefaultBg;
    /**
     * 图片是否加载完成,默认false
     */
    private boolean mImgLoaded;


    /**
     * 构造函数
     *
     * @param context Context
     */
    public TcAutoImageView(Context context) {
        super(context);
    }


    /**
     * 构造函数
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public TcAutoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造函数
     *
     * @param context  Context
     * @param attrs    AttributeSet
     * @param defStyle int
     */
    public TcAutoImageView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setImageResource(R.drawable.lib_default_img_big);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl 图片URL
     */
    public void setUrl(String aUrl) {
        setUrl(aUrl, null, true);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl          图片URL
     * @param aNeedMemCache 是否需要内存缓存
     */
    public void setUrl(String aUrl, boolean aNeedMemCache) {
        setUrl(aUrl, null, aNeedMemCache);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl 图片URL
     * @param aKey 如果图片URL会变，但有其他值不会变，则用此aKey做为关键字进行Hash唯一确定图片
     */
    public void setUrl(String aUrl, String aKey) {
        setUrl(aUrl, aKey, true);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl      图片URL
     * @param aPriority 优先级
     */
    public void setUrl(String aUrl, int aPriority) {
        setUrl(aUrl, null, true, aPriority);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl          图片URL
     * @param aKey          如果图片URL会变，但有其他值不会变，则用此aKey做为关键字进行Hash唯一确定图片
     * @param aNeedMemCache 是否需要内存缓存
     */
    public void setUrl(String aUrl, String aKey, boolean aNeedMemCache) {
        setUrl(aUrl, aKey, aNeedMemCache, 1);
    }

    /**
     * 设置图片的URL
     *
     * @param aUrl          图片URL
     * @param aKey          如果图片URL会变，但有其他值不会变，则用此aKey做为关键字进行Hash唯一确定图片
     * @param aNeedMemCache 是否需要内存缓存
     * @param aPriority     优先级
     */
    public void setUrl(String aUrl, String aKey, boolean aNeedMemCache, int aPriority) {

        setUrl(aUrl, aKey, aNeedMemCache, aPriority, 0, 0, false);

    }


    /**
     * @param aUrl     图片地址
     * @param aWidth   宽
     * @param aHight   高
     * @param aNeedCut 是否需要裁剪
     */
    public void setUrl(String aUrl, int aWidth, int aHight, boolean aNeedCut) {

        setUrl(aUrl, aWidth, aHight, aNeedCut, true);

    }

    /**
     * @param aUrl     图片地址
     * @param aWidth   宽
     * @param aHight   高
     * @param aNeedCut 是否需要裁剪
     */
    public void setUrl(String aUrl, int aWidth, int aHight, boolean aNeedCut, boolean aNeedCache) {

        setUrl(aUrl, null, aNeedCache, 1, aWidth, aHight, aNeedCut);

    }

    /**
     * @param aUrl           图片地址
     * @param aKey           标识
     * @param isNeedMemCache 是否缓存
     * @param aPriority      优先级
     * @param aWidth         宽
     * @param aHight         高
     * @param aNeedCut       是否裁剪
     */
    public void setUrl(String aUrl, String aKey, boolean isNeedMemCache, int aPriority, int aWidth, int aHight, boolean aNeedCut) {
        setUrl(aUrl, aKey, isNeedMemCache, aPriority, aWidth, aHight, aNeedCut, null);
    }

    /**
     * @param aUrl           图片地址
     * @param aKey           标识
     * @param isNeedMemCache 是否缓存
     * @param aPriority      优先级
     * @param aWidth         宽
     * @param aHight         高
     * @param aNeedCut       是否裁剪
     */
    public void setUrl(String aUrl, String aKey, boolean isNeedMemCache, int aPriority, int aWidth, int aHight, boolean aNeedCut, LoaderCallback callback) {
        mImgLoaded = true;
        mImgUrl = aUrl;
        mImgKey = aKey;
        mLoaderCallback = callback;
        ImageWrapper.loadImage(TcAutoImageView.this, aUrl, 0, aWidth, aHight, aNeedCut, isNeedMemCache, callback);
    }

    /**
     * 设置是否强制缩放到父View宽度
     *
     * @param aNeedFullWidth aNeedFullWidth
     */
    public void setNeedFullWidth(boolean aNeedFullWidth) {
        mNeedFullWidth = aNeedFullWidth;
    }

    /**
     * 设置Bitmap的回调处理器
     *
     * @param loaderCallback 回调处理解析完成的Bitmap
     */
    public void setLoaderCallback(LoaderCallback loaderCallback) {
        this.mLoaderCallback = loaderCallback;
    }

    /**
     * 设置是否启用图片加载完成后渐变显示效果
     *
     * @param aEnableFadeIn true代表开启
     * @param aStartAlpha   起始Alpha
     */
    public void enableFadeIn(boolean aEnableFadeIn, float aStartAlpha) {
        mEnableFadeIn = aEnableFadeIn;
        mStartAlpha = aStartAlpha;
    }

    /**
     * 是否将ImageView显示为圆形,需要将宽高设置为相等
     *
     * @param aEnableCircle true代表启用圆形显示
     */
    public void enableCircle(boolean aEnableCircle) {
        mEnableCircle = aEnableCircle;
    }

    /**
     * 设置边角的圆形半径
     *
     * @param aRadius 半径
     */
    public void setRadius(int aRadius) {
        mRadius = aRadius;
    }

    /**
     * 设置边角离边距的距离
     *
     * @param aRadiusMarin 边角离边距的距离
     */
    public void setRadiusMarin(int aRadiusMarin) {
        mRadiusMarin = aRadiusMarin;
    }

    /**
     * 是否启用边框颜色
     *
     * @param aEnable 是否使能
     */
    public void enableMarginColor(boolean aEnable) {
        mEnableMarginColor = aEnable;
    }

    /**
     * 设置边框颜色
     *
     * @param aMarinColor  边框颜色
     * @param aMarginWidth 边框宽度
     */
    public void setMarinColor(int aMarinColor, float aMarginWidth) {
        mMarginWidth = aMarginWidth;
        mMarginPaint = new Paint();
        mMarginPaint.setAntiAlias(true);
        mMarginPaint.setColor(aMarinColor);
        mMarginPaint.setStrokeCap(Paint.Cap.ROUND);
        mMarginPaint.setStyle(Paint.Style.STROKE);
        mMarginPaint.setStrokeWidth(aMarginWidth);
    }

    /**
     * true by default
     *
     * @param aEnableDefaultBg true or false
     */
    public void enableDefaultBg(boolean aEnableDefaultBg) {
        mNeedDefaultBg = aEnableDefaultBg;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            if (!TextUtils.isEmpty(mImgUrl) && !mImgLoaded) {
                setUrl(mImgUrl, mImgKey);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        // 如果设置了src,则不使用默认图，否则显示默认图
        if (getDrawable() == null && getBackground() == null && mNeedDefaultBg) {
            if (sDefaultBitmap == null) {
                sDefaultBitmap = BitmapFactory.decodeResource(getResources(),
                        TcResource.getResourceId("drawable", "lib_default_img_big"));
            }
            setImageBitmap(sDefaultBitmap);
        }
        super.onAttachedToWindow();
        if (!TextUtils.isEmpty(mImgUrl) && !mImgLoaded) {
            setUrl(mImgUrl, mImgKey);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
        ImageWrapper.cancelRequest(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNeedFullWidth) {
            if (getDrawable() instanceof BitmapDrawable) {
                setMinimumHeight(0);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = width * bitmap.getHeight() / bitmap.getWidth();
                    setMeasuredDimension(width, height);
                    return;
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLoadingStart() {

    }

    @Override
    public void onLoadingComplete(String aUrl, Bitmap aBitmap) {
        Log.d(TAG, "url = " + aUrl + " , aBitmap = " + aBitmap);
        if (aBitmap != null && !TextUtils.isEmpty(mImgUrl) && mImgUrl.equals(aUrl)) {
            mImgLoaded = true;
            if (mLoaderCallback != null) {
                mLoaderCallback.onSuccess(null, aUrl, aBitmap);
            } else {
                setImageBitmap(aBitmap);
            }
            if (mEnableFadeIn) {
                startFadeInAnimation();
            }
        }
    }

    /**
     * 设置默认颜色
     *
     * @param aColor 颜色值
     */
    public void setImageColor(int aColor) {
        if (mEnableCircle) {
            // 传-1过去，特殊处理
            TcRoundedDrawable drawable = new TcRoundedDrawable(aColor, -1, mRadiusMarin);
            setImageDrawable(drawable);
        } else if (mRadius > 0) {
            setImageDrawable(new TcRoundedDrawable(aColor, mRadius, mRadiusMarin));
        }
    }

    @Override
    public void setImageResource(int resId) {
        try {
            if (mEnableCircle || mRadius > 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                if (bitmap != null) {
                    setImageBitmap(bitmap);
                } else {
                    super.setImageResource(resId);
                }
            } else {
                super.setImageResource(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
            System.gc();
        }
    }

    @Override
    public void setImageBitmap(Bitmap aBitmap) {
        try {
            if (aBitmap != null) {
                if (mEnableCircle) {
                    // 传-1过去，特殊处理
                    setImageDrawable(new TcRoundedDrawable(aBitmap, -1, mRadiusMarin));
                } else if (mRadius > 0) {
                    setImageDrawable(new TcRoundedDrawable(aBitmap, mRadius, mRadiusMarin));
                } else {
                    super.setImageBitmap(aBitmap);
                }
            } else {
                super.setImageBitmap(aBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
            System.gc();
        }
    }

    /**
     * 开始渐变加载动画
     */
    private void startFadeInAnimation() {
        if (mFadeInAnimation == null) {
            mFadeInAnimation = new AlphaAnimation(mStartAlpha, 1);
            mFadeInAnimation.setDuration(DURATION_ANIM_FADE_IN);
            if (mDefaultInterpolator == null) {
                mDefaultInterpolator = new AccelerateInterpolator();
            }
            mFadeInAnimation.setInterpolator(mDefaultInterpolator);
        }
        startAnimation(mFadeInAnimation);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            // 规避一些地方使用不当,使用了回收的Bitmap导致崩溃的问题
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        if (mEnableMarginColor && mMarginPaint != null && getDrawable() != null) {
            if (getDrawable() instanceof TcRoundedDrawable) {
                mRectF.set(mRadiusMarin + mMarginWidth / 2, mRadiusMarin + mMarginWidth / 2,
                        getMeasuredWidth() - mMarginWidth / 2 - mRadiusMarin, getMeasuredHeight()
                                - mMarginWidth / 2 - mRadiusMarin);
                canvas.drawArc(mRectF, 0, ANGLE_MAX, false, mMarginPaint);
            } else {
                canvas.drawLine(0, 0, getMeasuredWidth(), 0, mMarginPaint);
                canvas.drawLine(getMeasuredWidth(), 0, getMeasuredWidth(), getMeasuredHeight(), mMarginPaint);
                canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), mMarginPaint);
                canvas.drawLine(0, 0, 0, getMeasuredHeight(), mMarginPaint);
            }

        }
    }
}
