/*
 * Filename:   PaTransformation.java
 * Description:
 * Copyright:   PingAnFang MIC Copyright(c)2015
 * @author:     Tamic
 * @version:    1.0
 * Create at:   2016-05-01
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * Created by LIUYONGKUI726 on 2016-09-06.
 */

package com.tamic.image.glidewarpper.core;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;


/**
 * Created by Tamic on 2016-09-06.
 */
public class TcTransformation implements Transformation {

    private int width;
    private int height;
    private String key;
    private Bitmap bitmap;

    public TcTransformation(int width, int height) {
        this(width, height, width + "*" + height);
    }


    public TcTransformation(int width, int height, String key) {
        this.width = width;
        this.height = height;
        this.key = key;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        Bitmap result;

        if (width == 0 || height == 0) {
            result = source;
        } else {
            result = Bitmap.createScaledBitmap(source, width, height, false);
        }

        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
        }
        bitmap = result;
        return result;
    }

    @Override
    public String key() {

        return key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
