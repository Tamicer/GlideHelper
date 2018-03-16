package com.tamic.image.glidewarpper.compressor;

import java.io.File;

public interface OnCompressListener {

    /**
     * 压缩开始时调用
     */
    void onStart();

    /**
     * 压缩成功时调用
     */
    void onSuccess(File file);

}
