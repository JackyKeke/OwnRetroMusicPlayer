package com.jackykeke.ownretromusicplayer.util.color;

/**
 * @author keyuliang on 2022/11/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Utility class for image analysis and processing.
 *
 * @hide
 */

public class ImageUtils {

    // Amount (max is 255) that two channels can differ before the color is no longer "gray".
    private static final int TOLERANCE = 20;
    // Alpha amount for which values below are considered transparent.
    private static final int AlPHA_TOLERANCE = 50;
    // Size of the smaller bitmap we're actually going to scan.
    private static final int COMPACT_BITMAP_SIZE = 64; // pixels
    private final Matrix mTempMatrix = new Matrix();

    private int[] mTempBuffer;
    private Bitmap mTempCompactBitmap;
    private Canvas mTempCompactBitmapCanvas;
    private Paint mTempCompactBitmapPaint;


    /**
     * Classifies a color as grayscale or not. Grayscale here means "very close to a perfect gray"; if
     * all three channels are approximately equal, this will return true.
     *
     * <p>Note that really transparent colors are always grayscale.
     */
    public static boolean isGrayscale(int color) {
        int alpha = 0XFF & (color >> 24);
        if (alpha < AlPHA_TOLERANCE) {
            return true;
        }

        int r = 0XFF & (color >> 16);
        int g = 0XFF & (color >> 8);
        int b = 0XFF & (color);

        return Math.abs(r - g) < TOLERANCE
                && Math.abs(r - b) < TOLERANCE
                && Math.abs(g - b) < TOLERANCE;

    }

    /**
     * Convert a drawable to a bitmap, scaled to fit within maxWidth and maxHeight.
     * 将可绘制对象转换为位图，缩放以适应 maxWidth 和 maxHeight。
     */
    public static Bitmap buildScaledBitmap(Drawable drawable, int maxWidth, int maxHeight) {
        if (drawable == null) {
            return null;
        }
        int originalWidth = drawable.getIntrinsicWidth();
        int originalHeight = drawable.getIntrinsicHeight();

        if ((originalWidth <= maxWidth)
                && (originalHeight <= maxHeight)
                && (drawable instanceof BitmapDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (originalHeight <= 0 || originalWidth <= 0) {
            return null;
        }

        // create a new bitmap, scaling down to fit the max dimensions of a large notification icon if necessary
        // 创建一个新的位图，如有必要，按比例缩小以适应大通知图标的最大尺寸
        float ratio = Math.min((float) maxWidth / (float) originalWidth, (float) maxHeight / (float) originalHeight);
        ratio = Math.min(1.0f, ratio);
        int scaledWidth = (int) (ratio * originalWidth);
        int scaledHeight = (int) (ratio * originalHeight);
        Bitmap result = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        // and paint our app bitmap on it
        Canvas canvas = new Canvas(result);
        drawable.setBounds(0, 0, scaledWidth, scaledHeight);
        drawable.draw(canvas);
        return result;

    }


    /**
     * Checks whether a bitmap is grayscale. Grayscale here means "very close to a perfect gray".
     *
     * <p>Instead of scanning every pixel in the bitmap, we first resize the bitmap to no more than
     * COMPACT_BITMAP_SIZE^2 pixels using filtering. The hope is that any non-gray color elements will
     * survive the squeezing process, contaminating the result with color.
     */
    public boolean isGrayscale(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        // shrink to a more manageable (yet hopefully no more or less colorful) size
        if (height > COMPACT_BITMAP_SIZE || width > COMPACT_BITMAP_SIZE) {
            if (mTempCompactBitmap == null) {
                mTempCompactBitmap = Bitmap.createBitmap(COMPACT_BITMAP_SIZE, COMPACT_BITMAP_SIZE, Bitmap.Config.ARGB_8888);
                mTempCompactBitmapCanvas = new Canvas(mTempCompactBitmap);
                mTempCompactBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mTempCompactBitmapPaint.setFilterBitmap(true);
            }
            mTempMatrix.reset();
            mTempMatrix.setScale((float) COMPACT_BITMAP_SIZE / width, (float) COMPACT_BITMAP_SIZE / height, 0, 0);

            mTempCompactBitmapCanvas.drawColor(0, PorterDuff.Mode.SRC); // select all, erase
            mTempCompactBitmapCanvas.drawBitmap(bitmap, mTempMatrix, mTempCompactBitmapPaint);
            bitmap = mTempCompactBitmap;
            width = height = COMPACT_BITMAP_SIZE;
        }

        final int size = height * width;
        ensureBufferSize(size);
        bitmap.getPixels(mTempBuffer, 0, width, 0, 0, width, height);

        for (int i = 0; i < size; i++) {
            if (!isGrayscale(mTempBuffer[i])){
                return false;
            }
        }

        return true;
    }

    /** Makes sure that {@code mTempBuffer} has at least length {@code size}. */
    private void ensureBufferSize(int size){
        if (mTempBuffer ==null || mTempBuffer.length < size){
            mTempBuffer = new int[size];
        }
    }

}
