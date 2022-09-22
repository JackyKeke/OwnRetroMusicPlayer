package com.jackykeke.ownretromusicplayer.extensions

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import com.jackykeke.appthemehelper.util.ATHUtil
import com.jackykeke.appthemehelper.util.ATHUtil.resolveColor

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