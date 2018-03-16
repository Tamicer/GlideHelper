package com.tamic.image.glidewarpper.core;

import android.graphics.Color;

/**
 * Created by Tamic on 2016-10-18.
 */
public enum LoadedFrom {

    MEMORY(Color.GREEN),
    DISK(Color.BLUE),
    NETWORK(Color.RED);

    final int debugColor;

    LoadedFrom(int debugColor) {
        this.debugColor = debugColor;
    }

}
