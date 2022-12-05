package com.jackykeke.ownretromusicplayer.util;

import android.graphics.Bitmap;

/**
 * @author keyuliang on 2022/12/2.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class ImageUtil {


    private ImageUtil() {
    }

    public static Bitmap resizeBitmap(Bitmap bm, int maxForSmallerSize) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        final int dstWidth;
        final int dstHeight;
        if (width < height) {
            if (maxForSmallerSize >= width) {
                return bm;
            }
            float ratio = (float) height / width;
            dstWidth = maxForSmallerSize;
            dstHeight = Math.round(maxForSmallerSize * ratio);
        } else {
            if (maxForSmallerSize >= height) {
                return bm;
            }
            float ratio = (float) width / height;
            dstWidth = Math.round(maxForSmallerSize * ratio);
            dstHeight = maxForSmallerSize;
        }
        return Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false);


    }

    public static int calculateInSampleSize(int width, int height, int reqWidth) {
        // setting reqWidth matching to desired 1:1 ratio and screen-size
        if (width < height) {
            reqWidth = height / width * reqWidth;
        } else {
            reqWidth = (width / height) * reqWidth;
        }

        int inSampleSize = 1;

        if (height > reqWidth || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            // 计算最大的 inSampleSize 值，它是 2 的幂并且保持高度和宽度都大于请求的高度和宽度。
            while ((halfHeight / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

        }
        return inSampleSize;

    }
}
