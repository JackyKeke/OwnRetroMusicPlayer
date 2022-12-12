package com.jackykeke.ownretromusicplayer.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import com.jackykeke.appthemehelper.util.ATHUtil
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.slider.Slider
import com.jackykeke.appthemehelper.ThemeStore
import com.jackykeke.appthemehelper.util.ColorUtil

/**
 *
 * @author keyuliang on 2022/9/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
fun Context.surfaceColor() = resolveColor(com.google.android.material.R.attr.colorSurface,Color.WHITE)

fun Context.resolveColor(@AttrRes attr:Int,fallBackColor:Int =0)=
    ATHUtil.resolveColor(this,attr,fallBackColor)


inline val @receiver:ColorInt Int.isColorLight
get() = ColorUtil.isColorLight(this)

@CheckResult
fun Drawable.tint(@ColorInt color: Int): Drawable {
    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    setTint(color)
    return tintedDrawable
}

fun Context.accentColor() = ThemeStore.accentColor(this)

fun Slider.applyColor(@ColorInt color: Int){
    ColorStateList.valueOf(color).run {
        thumbTintList = this
        trackActiveTintList = this
        trackInactiveTintList = ColorStateList.valueOf(color.addAlpha(0.1f))
        haloTintList= this
    }
}

fun @receiver:ColorInt Int.addAlpha(alpha: Float): Int {
    return ColorUtil.withAlpha(this, alpha)
}