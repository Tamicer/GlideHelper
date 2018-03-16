/*
 * Filename:    ImageLoaderHandler.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2015
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-05-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * Created by Tamic on 2016-09-01.
 */

package com.tamic.image.glidewarpper.core;

import android.graphics.Bitmap;

/**
 * 用于实现absImageView的自定义处理
 */
public interface IImageLoaderHandler {

    /**
     * 当图片加载开始
     */
    void onLoadingStart();

    /**
     * 当图片加载完成
     */
    void onLoadingComplete(String aUrl, Bitmap aBitmap);
}
