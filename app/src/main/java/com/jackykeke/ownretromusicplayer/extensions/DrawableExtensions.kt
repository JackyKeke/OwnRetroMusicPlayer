package com.jackykeke.ownretromusicplayer.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap

/**
 *
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */


fun Context.scaledDrawableResource(
    @DrawableRes id: Int,
    @DimenRes width: Int,
    @DimenRes height: Int
): Drawable {

    val w = resources.getDimension(width).toInt()
    val h = resources.getDimension(height).toInt()

    return scaledDrawable(id, w, h)
}


fun Context.scaledDrawable(@DrawableRes id: Int, width: Int, height: Int): Drawable {
    val bmp = BitmapFactory.decodeResource(resources, id)
    val bmpScaled = Bitmap.createScaledBitmap(bmp, width, height, false)
    return BitmapDrawable(resources, bmpScaled)
}

fun Drawable.toBitmap(scaleFactor: Float, config: Bitmap.Config? = null): Bitmap {
    return toBitmap((intrinsicHeight*scaleFactor).toInt(), (intrinsicWidth*scaleFactor).toInt(), config)
}
