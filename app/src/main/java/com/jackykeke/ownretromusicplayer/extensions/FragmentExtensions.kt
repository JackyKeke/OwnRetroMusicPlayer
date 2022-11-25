package com.jackykeke.ownretromusicplayer.extensions

import android.content.Context
import android.content.res.Configuration
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/9/26.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

val Context.generalThemeValue
    get() = PreferenceUtil.getGeneralThemeValue(isSystemDarkModelEnabled())

fun Context.isSystemDarkModelEnabled():Boolean{

    val isBatterySaverEnabled = (getSystemService<PowerManager>())?.isPowerSaveMode?:false
    val isDarkModeEnabled= (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    return isBatterySaverEnabled or isDarkModeEnabled

}

fun Context.isSystemDarkModeEnabled():Boolean{

    val isBatterySaverEnabled = getSystemService<PowerManager>()?.isPowerSaveMode ?: false

    val isDarkModeEnabled = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ) == Configuration.UI_MODE_NIGHT_YES

    return isBatterySaverEnabled or isDarkModeEnabled
}

