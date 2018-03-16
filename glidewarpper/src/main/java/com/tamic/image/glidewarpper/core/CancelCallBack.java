package com.tamic.image.glidewarpper.core;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * 取消回调
 * Created by Tamic on 2016-10-10.
 */
public interface CancelCallBack {

    void onBitmapLoaded(Bitmap bitmap, LoadedFrom from);


    void onBitmapFailed(Drawable errorDrawable);


    void onPrepareLoad(Drawable placeHolderDrawable);


}
