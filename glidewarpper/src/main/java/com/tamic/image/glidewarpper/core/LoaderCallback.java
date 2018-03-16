/*
 * Filename:    PaLoaderListener.java
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
 * 图片Loader监听器
 */
public interface LoaderCallback {

    void onSuccess(String key, String aUrl, Bitmap aBitmap);

    void onError();

}
