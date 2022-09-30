package com.jackykeke.ownretromusicplayer.glide.palette;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

/**
 * @author keyuliang on 2022/9/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class BitmapPaletteWrapper {

    private final Bitmap mBitmap;
    private final Palette mPalette;

    public BitmapPaletteWrapper(Bitmap bitmap, Palette palette) {
        mBitmap = bitmap;
        mPalette = palette;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Palette getPalette() {
        return mPalette;
    }

}
