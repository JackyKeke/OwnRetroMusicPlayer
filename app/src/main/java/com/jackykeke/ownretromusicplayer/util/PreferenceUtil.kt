package com.jackykeke.ownretromusicplayer.util

import androidx.preference.PreferenceManager
import com.jackykeke.ownretromusicplayer.App

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object PreferenceUtil {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val  defaultCategories = listOf(
        CategoryInfo(CategoryInfo.)
    )
}