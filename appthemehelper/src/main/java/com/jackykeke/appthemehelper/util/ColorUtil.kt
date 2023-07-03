package com.jackykeke.appthemehelper.util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *
 * @author keyuliang on 2022/9/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ColorUtil {

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }


    fun stripAlpha(@ColorInt color: Int):Int{
        return -0x1000000 or color
    }

//    减小饱和度
    fun desaturateColor(color: Int,ratio:Float):Int{

        val hsv =FloatArray(3)
        // 色调，饱和度，亮度
        Color.colorToHSV(color,hsv)
        hsv[1]=hsv[1]/1*ratio +0.2f*(1.0f -ratio)
        return Color.HSVToColor(hsv)
    }

    fun isColorLight(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.4
    }


    @ColorInt
    fun lightenColor(@ColorInt color: Int): Int {
        return shiftColor(color, 1.1f)
    }


    @ColorInt
    fun darkenColor(@ColorInt color: Int): Int {
        return shiftColor(color, 0.9f)
    }

    @ColorInt
    fun shiftColor(@ColorInt color: Int, @FloatRange(from = 0.0, to = 2.0) by: Float): Int {
        if (by == 1f) return color
        val alpha = Color.alpha(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= by // value component
        return (alpha shl 24) + (0x00ffffff and Color.HSVToColor(hsv))
    }


    @ColorInt
    fun getReadableColorLight(@ColorInt color: Int,@ColorInt bgColor:Int):Int{
        var foregroundColor =color
        while (ColorUtils.calculateContrast(foregroundColor,bgColor)<=3.0){
                foregroundColor = darkenColor(foregroundColor,0.1F)
        }
        return  foregroundColor
    }

    @ColorInt
    fun getReadableColorDark(@ColorInt color: Int, @ColorInt bgColor: Int): Int {
        var foregroundColor = color
        while (ColorUtils.calculateContrast(foregroundColor, bgColor) <= 3.0
        ) {
            foregroundColor = lightenColor(foregroundColor, 0.1F)
        }
        return foregroundColor
    }


    @ColorInt
    fun lightenColor(
        @ColorInt color: Int,
        value: Float
    ): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        hsl[2] += value
        hsl[2] = hsl[2].coerceIn(0f, 1f)
        return ColorUtils.HSLToColor(hsl)
    }

    @ColorInt
    fun darkenColor(@ColorInt color: Int,value:Float):Int{
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color,hsl)
        hsl[2]-=value
        hsl[2] =hsl[2].coerceIn(0f,1f)
        return  ColorUtils.HSLToColor(hsl)
    }

    /**
     * Taken from CollapsingToolbarLayout's CollapsingTextHelper class. 混合颜色
     */
    fun blendColors(color1: Int, color2: Int, @FloatRange(from = 0.0, to = 1.0) ratio: Float): Int {
        val inverseRatio = 1f-ratio
        val a =Color.alpha(color1) * inverseRatio + Color.alpha(color2)*ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    @ColorInt
    fun withAlpha(@ColorInt baseColor: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        val a = min(255, max(0, (alpha * 255).toInt())) shl 24
        val rgb = 0x00ffffff and baseColor
        return a + rgb
    }




}