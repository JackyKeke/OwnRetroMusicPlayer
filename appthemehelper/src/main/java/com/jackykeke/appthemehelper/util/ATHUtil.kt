package com.jackykeke.appthemehelper.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes

/**
 *
 * @author keyuliang on 2022/9/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ATHUtil {

    fun isWindowBackgroundDark(context: Context): Boolean {
        return !ColorUtil.isColorLight(resolveColor(context, android.R.attr.windowBackground))
    }

    @JvmOverloads
    fun resolveColor(context: Context, @AttrRes attr:Int,fallback:Int=0) :Int{
        context.theme.obtainStyledAttributes(intArrayOf(attr)).use {
            return try {
                it.getColor(0,fallback)
            }catch (e:Exception){
                Color.BLACK
            }
        }
    }

}