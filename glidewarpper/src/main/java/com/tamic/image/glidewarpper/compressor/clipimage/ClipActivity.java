package com.tamic.image.glidewarpper.compressor.clipimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tamic.image.glidewarpper.R;
import com.tamic.image.glidewarpper.compressor.clipimage.view.ClipViewLayout;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


public class ClipActivity extends Activity implements View.OnClickListener {

    private ClipViewLayout clip_circle, clip_rectangle;
    private TextView bt_ok, btn_cancel;
    private Intent intent;

    //1: circle, 2: rectangle
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lip);
        init();
        setListener();
    }

    private void init() {
        intent = getIntent();
        getWidget();
        type = intent.getIntExtra("type", 1);
    }

    private void getWidget() {
        clip_circle = (ClipViewLayout) findViewById(R.id.clip_circle);
        clip_rectangle = (ClipViewLayout) findViewById(R.id.clip_rectangle);
        bt_ok = (TextView) findViewById(R.id.bt_ok);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
    }

    private void setListener() {
        bt_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_ok) {
            generateUriAndReturn();
        }

        if (v.getId() == R.id.btn_cancel) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == 1) {
            clip_circle.setVisibility(View.VISIBLE);
            clip_rectangle.setVisibility(View.GONE);
            clip_circle.setImageSrc(intent.getData());
        } else {
            clip_rectangle.setVisibility(View.VISIBLE);
            clip_circle.setVisibility(View.GONE);
            clip_rectangle.setImageSrc(intent.getData());
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = clip_circle.clip();
        } else {
            zoomedCropBitmap = clip_rectangle.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
