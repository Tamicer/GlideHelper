/*
 * Filename:    ImageView.java
 * Description:
 * Copyright:   PingAnFang MIC Copyright(c)2016
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.WorkerThread;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tamic.image.glidewarpper.core.ImageResponseBody;

/**
 * 带过渡动画的ImageView
 * Created by Tamic on 2016-09-01.
 */

@SuppressLint("AppCompatCustomView")
public class TcImageView extends ImageView implements ImageResponseBody.ProgressListener {

    private final int MAX_PROGRESS = 100;
    private Paint mArcPaint;
    private RectF mBound;
    private Paint mCirclePaint;
    private int mProgress = 0;
    private String mTag;

    public TcImageView(Context context) {
        this(context, null, 0);
    }

    public TcImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TcImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    @WorkerThread
    public void setProgress(@IntRange(from = 0, to = MAX_PROGRESS) int mProgress) {
        this.mProgress = mProgress;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void init() {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mArcPaint.setStrokeWidth(dpToPixel(0.1f, getContext()));
        mArcPaint.setColor(Color.argb(120, 0xff, 0xff, 0xff));

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(dpToPixel(2, getContext()));
        mCirclePaint.setColor(Color.argb(120, 0xff, 0xff, 0xff));
        mBound = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int min = Math.min(w, h);
        int max = w + h - min;
        int r = min / 5;
        //set up a square in the imageView
        mBound.set(max / 2 - r, min / 2 - r, max / 2 + r, min / 2 + r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress != MAX_PROGRESS && mProgress != 0 && mProgress < MAX_PROGRESS) {
            float mAngle = mProgress * 360f / MAX_PROGRESS;
            canvas.drawArc(mBound, 270, mAngle, true, mArcPaint);
            canvas.drawCircle(mBound.centerX(), mBound.centerY(), mBound.height() / 2, mCirclePaint);

        }
    }

    private float scale = 0;

    private int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);

    }

    @Override
    public void update(@IntRange(from = 0, to = MAX_PROGRESS) int percent, String url) {
        if (!url.equals(mTag)) {
            return;
        }
        this.mProgress = percent;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
