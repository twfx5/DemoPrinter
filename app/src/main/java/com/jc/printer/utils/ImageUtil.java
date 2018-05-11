package com.jc.printer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.decodeResourceStream;

/**
 * Created by biabia on 2018/2/24
 */

public class ImageUtil {
    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
        Bitmap bm = null;
        InputStream is = null;
        if (opts == null) {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
        }
        try {
            final TypedValue value = new TypedValue();
            is = res.openRawResource(id, value);
            bm = decodeResourceStream(res, value, is, null, opts);
        } catch (Exception e) {
            // Ignore
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        if (bm == null && opts != null && opts.inBitmap != null) {
            throw new IllegalArgumentException("Problem decoding into existing bitmap");
        }
        return bm;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 根据inSampleSize压缩图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static InputStream compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10\
            if (options <= 20) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isBm;
    }

    //reqWidth和reqHeight是需要显示的Imageview的width和height
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // height和width图片长宽的像素
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        if (newWidth != width && newHeight != height) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
            return newbm;
        } else {
            return copy(bm);
        }
    }

    private static PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    // 缩放图片 并旋转 并加尾巴
    public static Bitmap zoomImgAndOffsetAndRotate(Bitmap bm, int offsetX, int offsetY, int newWidth, int newHeight, int rotate, int cabel_orientatioin, int cabel_length) {
        if (bm == null) {
            return Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        }
        Matrix matrix = new Matrix();
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width != newWidth || height != newHeight) {
            final float sx = newWidth / (float) width;
            final float sy = newHeight / (float) height;
            matrix.setScale(sx, sy);
        }
        matrix.postRotate(rotate);
        // 创建新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);

        width = newbm.getWidth();
        height = newbm.getHeight();
        if (cabel_orientatioin != -1) {
            if (rotate == 90) {//0上 1右 2下 3左
                cabel_orientatioin += 1;
            } else if (rotate == 180) {//0上 1右 2下 3左
                cabel_orientatioin += 2;
            } else if (rotate == 270) {//0上 1右 2下 3左
                cabel_orientatioin += 3;
            }
            cabel_orientatioin = cabel_orientatioin % 4;
        }
        offsetX = offsetX * 8;
        offsetY = offsetY * 8;
        switch (cabel_orientatioin) {
            case 0:
                offsetY += cabel_length * 8;
            case 2:
                height += cabel_length * 8;
                break;
            case 1:
                offsetX += cabel_length * 8;
            case 3:
                width += cabel_length * 8;
                break;
        }

        Bitmap offsetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(offsetBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(0xffffffff);
        RectF rect = new RectF(0, 0, width, height);
        paint.setXfermode(null);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.setDrawFilter(pfd);
        canvas.drawRect(rect, paint);
        canvas.drawBitmap(newbm, offsetX, offsetY, null);
        if (!bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        if (!newbm.isRecycled()) {
            newbm.recycle();
            newbm = null;
        }
        return offsetBitmap;
    }

    public static Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);//设置线条等图形的抗锯齿
        canvas.setDrawFilter(pfd);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    public static Bitmap copy(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
