package com.jackykeke.ownretromusicplayer.glide.palette;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.jackykeke.ownretromusicplayer.util.RetroColorUtil;

/**
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class BitmapPaletteTranscoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {

    @Nullable
    @Override
    public Resource<BitmapPaletteWrapper> transcode(@NonNull Resource<Bitmap> toTranscode, @NonNull Options options) {
        Bitmap bitmap = toTranscode.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap, RetroColorUtil.generatePalette(bitmap));

        return new BitmapPaletteResource(bitmapPaletteWrapper);
    }
}
