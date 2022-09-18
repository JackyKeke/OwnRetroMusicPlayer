package com.jackykeke.appthemehelper

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import com.jackykeke.appthemehelper.util.TintHelper

/**
 *
 * @author keyuliang on 2022/9/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ATH {


    fun didThemeValuesChange(context: Context,since:Long) :Boolean = ThemeStore.isConfigured(context)
            &&ThemeStore.prefs(context).getLong(ThemeStorePrefKeys.VALUES_CHANGED,-1)>since

    fun setTint(view: View,@ColorInt color:Int){
        TintHelper.setTintAuto(view,color,false)
    }

    fun setBackgroundTint(view: View, @ColorInt color: Int) {
        TintHelper.setTintAuto(view, color, true)
    }


}