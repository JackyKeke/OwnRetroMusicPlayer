package com.jackykeke.ownretromusicplayer.extensions

import android.app.Activity
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 *
 * @author keyuliang on 2023/1/3.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun Fragment.dipToPix(dpInFloat: Float): Float {
    val scale = resources.displayMetrics.density
    return dpInFloat * scale + 0.5f
}

fun Activity.dipToPix(dpInFloat: Float): Float {
    val scale = resources.displayMetrics.density
    return dpInFloat * scale + 0.5f
}

fun AppCompatActivity.dimToPixel(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}
