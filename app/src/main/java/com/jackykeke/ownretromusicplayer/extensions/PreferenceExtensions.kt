package com.jackykeke.ownretromusicplayer.extensions

import android.content.SharedPreferences

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun SharedPreferences.getStringOrDefault(key:String,default: String) : String = getString(key,default)?:default