/*
 * Filename:    PaRoundedDrawable.java
 * Description:
 * Copyright:   PingAnFang MIC Copyright(c)2016
 * @author:     Liuyongkui
 * @version:    1.0
 * Create at:   2016-09-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * Created by Tamic on 2016-09-01.
 */

package com.tamic.image.glidewarpper.content;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * 圆角显示的Drawable,可以实现图片以圆角矩形和正圆形展示的效果
 * Created by Tamic on 2016-09-01.
 */
public class TcRoundedDrawable extends Drawable {

    /**
     * 圆角半径
     */
    protected final float mCornerRadius;
    /**
     * 边距
     */
    protected final int mMargin;
    /**
     * RectF
     */
    protected final RectF mRect = new RectF();
    /**
     * RectF
     */
    protected RectF mBitmapRect;
    /**
     * 图片渲染绘制
     */
    protected BitmapShader bitmapShader;
    /**
     * Paint
     */
    protected final Paint mPaint;
    /**
     * 颜色色值
     */
    protected int mColor;
    /**
     * 抗锯齿
     */
    private static PaintFlagsDrawFilter sPdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
            | Paint.FILTER_BITMAP_FLAG);

    /**
     * Bitmap圆形或圆角矩形构造函数
     *
     * @param aBitmap       图片
     * @param aCornerRadius 圆角半径
     * @param aMargin       图片离圆角的边距
     */
    public TcRoundedDrawable(Bitmap aBitmap, int aCornerRadius, int aMargin) {
        this.mCornerRadius = aCornerRadius;
        this.mMargin = aMargin;

        bitmapShader = new BitmapShader(aBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapRect = new RectF(0, 0, aBitmap.getWidth(), aBitmap.getHeight());

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setShader(bitmapShader);
    }

    /**
     * 纯色圆形或圆角矩形构造函数
     *
     * @param aColor        颜色
     * @param aCornerRadius 圆角半径
     * @param aMargin       图片离圆角的边距
     */
    public TcRoundedDrawable(int aColor, float aCornerRadius, int aMargin) {
        this.mColor = aColor;
        this.mCornerRadius = aCornerRadius;
        this.mMargin = aMargin;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(aColor);
    }

//	@Override
//	public int getIntrinsicWidth() {
//		return mBitmapRect != null ? (int) mBitmapRect.width() : super.getIntrinsicWidth();
//	}
//
//	@Override
//	public int getIntrinsicHeight() {
//		return mBitmapRect != null ? (int) mBitmapRect.height() : super.getIntrinsicHeight();
//	}

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRect.set(mMargin, mMargin, bounds.width() - mMargin, bounds.height() - mMargin);

        if (bitmapShader != null) {
            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(shaderMatrix);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        if (mCornerRadius < 0) {
            canvas.setDrawFilter(sPdf);
            float radius = Math.max(mRect.bottom - mRect.top, mRect.right - mRect.left);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(mRect, radius, radius, mPaint);
        } else {
            canvas.setDrawFilter(sPdf);
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
        }
        canvas.restore();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
}
