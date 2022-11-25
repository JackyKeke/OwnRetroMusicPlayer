package com.jackykeke.ownretromusicplayer.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.palette.graphics.Palette;

/**
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class RetroColorUtil {

    public static  int desaturateColor(int color, float ratio ){
        float[] hsv =new float[3];
        Color.colorToHSV(color,hsv);
        hsv[1] =(hsv[1]*ratio)+(0.2f*(1f-ratio));
        return Color.HSVToColor(hsv);
    }


    public static Palette generatePalette(Bitmap bitmap){
        return bitmap == null ?null:Palette.from(bitmap).clearFilters().generate();

    }

    public static int getTextColor( Palette palette){
        if (palette == null){
            return -1;
        }

        int inverse = -1;

        if (palette.getVibrantSwatch()!=null){
            inverse = palette.getVibrantSwatch().getRgb();
        }else if (palette.getLightVibrantSwatch() != null) {
            inverse = palette.getLightVibrantSwatch().getRgb();
        } else if (palette.getDarkVibrantSwatch() != null) {
            inverse = palette.getDarkVibrantSwatch().getRgb();
        }

    }


}
