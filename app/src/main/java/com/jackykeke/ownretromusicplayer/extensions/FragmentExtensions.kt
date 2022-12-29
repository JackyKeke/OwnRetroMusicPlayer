package com.jackykeke.ownretromusicplayer.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
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

fun Context.isSystemDarkModelEnabled(): Boolean {

    val isBatterySaverEnabled = (getSystemService<PowerManager>())?.isPowerSaveMode ?: false
    val isDarkModeEnabled =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    return isBatterySaverEnabled or isDarkModeEnabled

}

fun Context.isSystemDarkModeEnabled(): Boolean {

    val isBatterySaverEnabled = getSystemService<PowerManager>()?.isPowerSaveMode ?: false

    val isDarkModeEnabled =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    return isBatterySaverEnabled or isDarkModeEnabled
}

@Suppress("UNCHECKED_CAST")
fun <T> AppCompatActivity.whichFragment(@IdRes id: Int): T =
    supportFragmentManager.findFragmentById(id) as T

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.whichFragment(@IdRes id:Int):T = childFragmentManager.findFragmentById(id) as T



fun Fragment.showToast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(getString(stringRes), duration)
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable {
    return AppCompatResources.getDrawable(this, drawableRes)!!
}

fun Fragment.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable {
    return AppCompatResources.getDrawable(requireContext(), drawableRes)!!
}

inline fun <reified T:Any> Fragment.extra(key:String,default:T?=null) = lazy {
    val value = arguments?.get(key)
    if (value is T) value else default
}

inline  fun <reified T : Any> Fragment.extraNotNull(key: String, default: T? = null) = lazy{
    val value = arguments?.get(key)
    requireNotNull(if (value is T) value else default){ key }
}