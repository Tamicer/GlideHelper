package com.tamic.image.glidewarpper.compressor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;


public class Compressor {

    public static volatile Compressor compressor = null;

    public static Compressor getCompressor() {
        if (compressor == null) {
            synchronized (Compressor.class) {
                if (compressor == null) {
                    compressor = new Compressor();
                }
            }
        }
        return compressor;
    }

    private Compressor() {
    }

    public Bitmap rotate(Bitmap src, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e("Compressor error", "Compressor rotate  OutOfMemoryError");
        }
        return src;

    }

    /**
     * 图片大小的缩放方法
     *
     * @param src   ：源图片资源
     * @param scale ：缩放比例
     */
    public Bitmap scale(Bitmap src, float scale) {
        try {
            return scale(src, scale, scale);
        } catch (OutOfMemoryError e) {
            Log.e("Compressor error", "Compressor scale  OutOfMemoryError");
        }
        return src;
    }

    /**
     * 图片大小的缩放方法
     *
     * @param path ：源图片资源
     */
    public Bitmap scale(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = this.calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 图片大小的缩放方法
     *
     * @param src    ：源图片资源
     * @param width  ：横向缩放比例
     * @param height ：纵向缩放比例
     */
    public static Bitmap scale(Bitmap src, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, width, height);
        return Bitmap.createBitmap(src, 0, 0, options.outWidth, options.outWidth);
    }

    /**
     * 图片大小的缩放方法
     *
     * @param src    ：源图片资源
     * @param scaleX ：横向缩放比例
     * @param scaleY ：纵向缩放比例
     */
    public Bitmap scale(Bitmap src, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        try {
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e("Compressor error", "Compressor scale  OutOfMemoryError");
        }
        return src;
    }

    /**
     * 图片大小的缩放方法
     *
     * @param src         ：源图片资源
     * @param scaleMatrix ：缩放规则
     */
    public Bitmap scale(Bitmap src, Matrix scaleMatrix) {
        try {
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
                    scaleMatrix, true);
        } catch (OutOfMemoryError e) {
            Log.e("Compressor error", "Compressor scale  OutOfMemoryError");
        }
        return src;
    }

    /**
     * 图片大小的缩放方法
     *
     * @param bitmap 源Bitmap
     * @param w      宽
     * @param h      高
     * @return 目标Bitmap
     */
    public Bitmap zoom(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e("Compressor error", "Compressor scale  OutOfMemoryError");
        }
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
