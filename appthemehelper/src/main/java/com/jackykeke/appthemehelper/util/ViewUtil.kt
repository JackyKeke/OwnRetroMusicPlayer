package com.jackykeke.appthemehelper.util

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ViewUtil {

    fun removeOnGlobalLayoutListener(v: View,listener:ViewTreeObserver.OnGlobalLayoutListener){
        v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    fun setBackgroundCompat(view: View, drawable: Drawable?) {
        view.background = drawable
    }
}