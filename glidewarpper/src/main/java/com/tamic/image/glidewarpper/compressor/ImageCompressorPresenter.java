package com.tamic.image.glidewarpper.compressor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.tamic.image.glidewarpper.compressor.clipimage.ClipActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.tamic.image.glidewarpper.util.NullUtils.checkNotNull;


/**
 * Created by Tamic on 16/8/24.
 */
public class ImageCompressorPresenter {

    static Compressor compressor = Compressor.getCompressor();

    /*默认图片加载尺寸*/
    private static final int DEFAULT_IMAGE_SIZE = 300;
    /*裁剪返回tag值*/
    private static final int REQUEST_CROP_PHOTO = 102;

    private ImageCompressorPresenter() {
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 要旋转的图片
     * @return 旋转后的图片
     */
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        if (angle == 0 | angle % 360 == 0) {
            return bitmap;
        }
        return compressor.rotate(bitmap, angle);

    }

    /**
     * 旋转图片
     *
     * @param angle     旋转角度
     * @param imagePath 图片的本地URL
     * @return 旋转后的图片
     */
    public static Bitmap rotate(String imagePath, int angle) {
        if (angle == 0 | angle % 360 == 0) {
            return BitmapFactory.decodeFile(imagePath);
        }
        return compressor.rotate(BitmapFactory.decodeFile(imagePath), angle);

    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param src   ：源图片资源
     * @param scale ：缩放比例
     */
    public static Bitmap scale(Bitmap src, float scale) {
        if (scale >= 1.3 || scale <= 0) {
            return src;
        }
        return compressor.scale(src, scale);

    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param imagePath ：图片的本地URL
     * @param scale     ：缩放比例
     */
    public static Bitmap scale(String imagePath, float scale) {
        if (scale >= 1.3 || scale <= 0) {
            return BitmapFactory.decodeFile(imagePath);
        }
        return compressor.scale(BitmapFactory.decodeFile(imagePath), scale);

    }


    /**
     * 图片大小的缩放方法(无损)
     *
     * @param src    ：源图片资源
     * @param scaleX ：横向缩放比例
     * @param scaleY ：纵向缩放比例
     */
    public static Bitmap scale(Bitmap src, float scaleX, float scaleY) {
        if (scaleX <= 0 || scaleX >= 1.3f) {
            scaleX = 1.0f;
        }
        if (scaleY <= 0 || scaleY >= 1.3f) {
            scaleY = 1.0f;
        }
        if (scaleX == 1.0f && scaleY == 1.0f) {
            return src;
        }
        return compressor.scale(src, scaleX, scaleY);

    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param imagePath ：图片的本地URL
     * @param scaleX    ：横向缩放比例
     * @param scaleY    ：纵向缩放比例
     */
    public static Bitmap scale(String imagePath, float scaleX, float scaleY) {
        if (scaleY <= 0 || scaleX >= 1.3f) {
            scaleX = 1.0f;
        }
        if (scaleY <= 0 || scaleY >= 1.3f) {
            scaleY = 1.0f;
        }
        if (scaleX == 1.0f && scaleY == 1.0f) {
            return BitmapFactory.decodeFile(imagePath);
        }
        return compressor.scale(BitmapFactory.decodeFile(imagePath), scaleX, scaleY);

    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param src         ：源图片资源
     * @param scaleMatrix ：缩放规则
     */
    public static Bitmap scale(Bitmap src, Matrix scaleMatrix) {
        if (scaleMatrix == null) {
            return src;
        }
        return compressor.scale(src, scaleMatrix);

    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param imagePath   ：图片的本地URL
     * @param scaleMatrix ：缩放规则
     */
    public static Bitmap scale(String imagePath, Matrix scaleMatrix) {
        if (scaleMatrix == null) {
            return BitmapFactory.decodeFile(imagePath);
        }
        return compressor.scale(BitmapFactory.decodeFile(imagePath), scaleMatrix);

    }

    /**
     * 图片大小的缩放方法(无损)
     */
    public static Bitmap scale(String path, int width, int height) {
        return compressor.scale(path, width, height);

    }

    /**
     * 图片大小的缩放方法
     *
     * @param bitmap ：位图
     * @param width  ：横向缩放比例
     * @param height ：纵向缩放比例
     */
    public static Bitmap scale(Bitmap bitmap, int width, int height) {
        return compressor.scale(bitmap, width, height);
    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param bitmap 源Bitmap
     * @param w      宽
     * @param h      高
     * @return 目标Bitmap
     */
    public static Bitmap zoom(Bitmap bitmap, int w, int h) {
        if (w <= 0 && h <= 0) {
            return bitmap;
        }
        if (w <= 0) {
            w = DEFAULT_IMAGE_SIZE;
        }
        if (h <= 0) {
            h = DEFAULT_IMAGE_SIZE;
        }
        return compressor.zoom(bitmap, w, h);
    }

    /**
     * 图片大小的缩放方法(无损)
     *
     * @param imagePath 图片地址
     * @param w         宽
     * @param h         高
     * @return 目标Bitmap
     */
    public static Bitmap zoom(String imagePath, int w, int h) {
        if (w <= 0 && h <= 0) {
            return BitmapFactory.decodeFile(imagePath);
        }
        if (w <= 0) {
            w = DEFAULT_IMAGE_SIZE;
        }
        if (h <= 0) {
            h = DEFAULT_IMAGE_SIZE;
        }
        return compressor.zoom(BitmapFactory.decodeFile(imagePath), w, h);
    }

    /**
     * 剪裁图片
     *
     * @param activity    提出跳转的页面
     * @param type        剪裁框类型(1:圆,2:方)
     * @param data        拍照/选择图片返回的uri
     * @param requestCode 作为返回判断用的code值
     */
    public static void clipImage(Activity activity, int type, Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, ClipActivity.class);
        if (type <= 0 || type > 2) {
            type = 1;
        }
        intent.putExtra("type", type);
        if (data == null) {
            return;
        }
        intent.setData(data);
        activity.startActivityForResult(intent, requestCode == 0 ? REQUEST_CROP_PHOTO : requestCode);
    }

    public static class Luban {

        public static final int FIRST_GEAR = 1;
        public static final int THIRD_GEAR = 3;
        public static String DEFAULT_DISK_CACHE_DIR = "pinganfang_image_cache";
        private static final String TAG = "Luban";

        private static volatile Luban INSTANCE;
        private final File mCacheDir;
        private File mFile;
        private OnCompressListener compressListener;

        private int gear = THIRD_GEAR;
        private String filename;

        Luban(File cacheDir) {
            mCacheDir = cacheDir;
        }

        /**
         * Returns a directory with a default name in the private cache directory of the application to use to store
         * retrieved media and thumbnails.
         *
         * @param context A context.
         * @see #getPhotoCacheDir(Context, String)
         */
        static File getPhotoCacheDir(Context context) {
            return getPhotoCacheDir(context, Luban.DEFAULT_DISK_CACHE_DIR);
        }

        /**
         * Returns a directory with the given name in the private cache directory of the application to use to store
         * retrieved media and thumbnails.
         *
         * @param context   A context.
         * @param cacheName The name of the subdirectory in which to store the cache.
         * @see #getPhotoCacheDir(Context)
         */
        static File getPhotoCacheDir(Context context, String cacheName) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null) {
                File result = new File(cacheDir + File.separator + cacheName);
                if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                    // File wasn't able to create a directory, or the result exists but not a directory
                    return null;
                }
                return result;
            }
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "default disk cache dir is null");
            }
            return null;
        }

        public static Luban get(Context context) {
            if (INSTANCE == null) {
                INSTANCE = new Luban(Luban.getPhotoCacheDir(context));
            }
            return INSTANCE;
        }

        public Luban launch() {
            checkNotNull(mFile, "the image_load_err file cannot be null, please call .load() before this method!");

            if (compressListener != null) {
                compressListener.onStart();
            }

            if (gear == Luban.FIRST_GEAR) {
                if (compressListener != null) {
                    compressListener.onSuccess(firstCompress(mFile));
                }
            } else if (gear == Luban.THIRD_GEAR) {
                if (compressListener != null) {
                    compressListener.onSuccess(thirdCompress(mFile));
                }
            }
            return this;
        }

        public Luban load(File file) {
            mFile = file;
            return this;
        }

        public Luban setCompressListener(OnCompressListener listener) {
            compressListener = listener;
            return this;
        }

        public Luban putGear(int gear) {
            this.gear = gear;
            return this;
        }

        public Luban setFilename(String filename) {
            this.filename = filename;
            return this;
        }


        private File thirdCompress(@NonNull File file) {
            String thumb = mCacheDir.getAbsolutePath() + File.separator + (TextUtils.isEmpty(filename) ? System.currentTimeMillis() : filename);

            double size;
            String filePath = file.getAbsolutePath();
            int width = getImageSize(filePath)[0];
            int height = getImageSize(filePath)[1];
            int thumbW = width % 2 == 1 ? width + 1 : width;
            int thumbH = height % 2 == 1 ? height + 1 : height;

            width = thumbW > thumbH ? thumbH : thumbW;
            height = thumbW > thumbH ? thumbW : thumbH;

            double scale = ((double) width / height);

            if (scale <= 1 && scale > 0.5625) {
                if (height < 1664) {
                    if (file.length() / 1024 < 150) {
                        return file;
                    }

                    size = (width * height) / Math.pow(1664, 2) * 150;
                    size = size < 60 ? 60 : size;
                } else if (height >= 1664 && height < 4990) {
                    thumbW = width / 2;
                    thumbH = height / 2;
                    size = (thumbW * thumbH) / Math.pow(2495, 2) * 300;
                    size = size < 60 ? 60 : size;
                } else if (height >= 4990 && height < 10240) {
                    thumbW = width / 4;
                    thumbH = height / 4;
                    size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                    size = size < 100 ? 100 : size;
                } else {
                    int multiple = height / 1280 == 0 ? 1 : height / 1280;
                    thumbW = width / multiple;
                    thumbH = height / multiple;
                    size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                    size = size < 100 ? 100 : size;
                }
            } else if (scale <= 0.5625 && scale > 0.5) {
                if (height < 1280 && file.length() / 1024 < 200) {
                    return file;
                }

                int multiple = height / 1280 == 0 ? 1 : height / 1280;
                thumbW = width / multiple;
                thumbH = height / multiple;
                size = (thumbW * thumbH) / (1440.0 * 2560.0) * 400;
                size = size < 100 ? 100 : size;
            } else {
                int multiple = (int) Math.ceil(height / (1280.0 / scale));
                thumbW = width / multiple;
                thumbH = height / multiple;
                size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 500;
                size = size < 100 ? 100 : size;
            }

            return compress(filePath, thumb, thumbW, thumbH, (long) size);
        }

        private File firstCompress(@NonNull File file) {
            int minSize = 60;
            int longSide = 720;
            int shortSide = 1280;

            String filePath = file.getAbsolutePath();
            String thumbFilePath = mCacheDir.getAbsolutePath() + File.separator + (TextUtils.isEmpty(filename) ? System.currentTimeMillis() : filename);

            long size = 0;
            long maxSize = file.length() / 5;

            int[] imgSize = getImageSize(filePath);
            int width = 0, height = 0;
            if (imgSize[0] <= imgSize[1]) {
                double scale = (double) imgSize[0] / (double) imgSize[1];
                if (scale <= 1.0 && scale > 0.5625) {
                    width = imgSize[0] > shortSide ? shortSide : imgSize[0];
                    height = width * imgSize[1] / imgSize[0];
                    size = minSize;
                } else if (scale <= 0.5625) {
                    height = imgSize[1] > longSide ? longSide : imgSize[1];
                    width = height * imgSize[0] / imgSize[1];
                    size = maxSize;
                }
            } else {
                double scale = (double) imgSize[1] / (double) imgSize[0];
                if (scale <= 1.0 && scale > 0.5625) {
                    height = imgSize[1] > shortSide ? shortSide : imgSize[1];
                    width = height * imgSize[0] / imgSize[1];
                    size = minSize;
                } else if (scale <= 0.5625) {
                    width = imgSize[0] > longSide ? longSide : imgSize[0];
                    height = width * imgSize[1] / imgSize[0];
                    size = maxSize;
                }
            }

            return compress(filePath, thumbFilePath, width, height, size);
        }

        /**
         * obtain the image_load_err's width and height
         *
         * @param imagePath the path of image_load_err
         */
        public int[] getImageSize(String imagePath) {
            int[] res = new int[2];

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeFile(imagePath, options);

            res[0] = options.outWidth;
            res[1] = options.outHeight;

            return res;
        }

        /**
         * obtain the thumbnail that specify the size
         *
         * @param imagePath the target image_load_err path
         * @param width     the width of thumbnail
         * @param height    the height of thumbnail
         * @return {@link Bitmap}
         */
        private Bitmap compress(String imagePath, int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            int outH = options.outHeight;
            int outW = options.outWidth;
            int inSampleSize = 1;

            if (outH > height || outW > width) {
                int halfH = outH / 2;
                int halfW = outW / 2;

                while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                    inSampleSize *= 2;
                }
            }

            options.inSampleSize = inSampleSize;

            options.inJustDecodeBounds = false;

            int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
            int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

            if (heightRatio > 1 || widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    options.inSampleSize = heightRatio;
                } else {
                    options.inSampleSize = widthRatio;
                }
            }
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(imagePath, options);
        }

        /**
         * 指定参数压缩图片
         * create the thumbnail with the true rotate angle
         *
         * @param largeImagePath the big image_load_err path
         * @param thumbFilePath  the thumbnail path
         * @param width          width of thumbnail
         * @param height         height of thumbnail
         * @param size           the file size of image_load_err
         */
        private File compress(String largeImagePath, String thumbFilePath, int width, int height, long size) {
            Bitmap thbBitmap = compress(largeImagePath, width, height);

            return saveImage(thumbFilePath, thbBitmap, size);
        }

        /**
         * 保存图片到指定路径
         * Save image_load_err with specified size
         *
         * @param filePath the image_load_err file save path 储存路径
         * @param bitmap   the image_load_err what be save   目标图片
         * @param size     the file size of image_load_err   期望大小
         */
        private File saveImage(String filePath, Bitmap bitmap, long size) {
            checkNotNull(bitmap, TAG + "bitmap cannot be null");

            File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

            if (!result.exists() && !result.mkdirs()) {
                return null;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

            while (stream.toByteArray().length / 1024 > size && options > 6) {
                stream.reset();
                options -= 6;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
            }

            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(stream.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new File(filePath);
        }
    }
}