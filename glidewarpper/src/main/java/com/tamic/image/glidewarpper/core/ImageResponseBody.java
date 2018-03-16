package com.tamic.image.glidewarpper.core;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by LIUYONGKUI726 on 2016-09-06.
 */
public class ImageResponseBody extends ResponseBody{

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;
    private Handler handler;
    private String url;

    public ImageResponseBody(ResponseBody responseBody, String url, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.url = url;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                Log.v("ImageResponseBody", "no callback load ：>>> "+ totalBytesRead + "");
                if (progressListener != null && url != null) {
                    Log.v("ImageResponseBody", "callback load ：>>> "+totalBytesRead + "");
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressListener.update(
                                    ((int) ((100 * totalBytesRead) / responseBody.contentLength())), url);

                        }
                    });


                }
                return bytesRead;
            }
        };
    }

    public interface ProgressListener {
        @WorkerThread
        void update(@IntRange(from = 0, to = 100) int percent, @NonNull String url);
    }
}
