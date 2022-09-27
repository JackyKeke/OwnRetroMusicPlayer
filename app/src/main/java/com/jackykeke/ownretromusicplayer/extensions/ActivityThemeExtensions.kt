package com.jackykeke.ownretromusicplayer.extensions

import android.app.ActivityManager
import android.graphics.Color
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.utils.MDUtil.isColorDark
import com.google.android.material.internal.EdgeToEdgeUtils.setLightNavigationBar
import com.jackykeke.appthemehelper.util.ColorUtil
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/9/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */


fun AppCompatActivity.setTaskDescriptionColorAuto() {
    setTaskDescriptionColor(surfaceColor())
}

fun FragmentActivity.setTaskDescriptionColor(color: Int) {

    var colorFinal = color
    // Task description requires fully opaque color
    colorFinal = ColorUtil.stripAlpha(colorFinal)
    // Sets color of entry in the system recents page
    if (VersionUtils.hasP()) {
        setTaskDescription(
            ActivityManager.TaskDescription(title as String?, -1, colorFinal)
        )
    } else {
        setTaskDescription(ActivityManager.TaskDescription(title as String?))
    }

}

fun AppCompatActivity.setLightNavigationBarAuto() {
    setLightNavigationBar(surfaceColor().isColorLight)
}

fun AppCompatActivity.setLightStatusBarAuto(bgColor:Int){
    setLightStatusBar(bgColor.isColorLight)
}

fun AppCompatActivity.setForceDarkAllowed(boolean: Boolean){
    if (VersionUtils.hasQ()){
        window.decorView.isForceDarkAllowed = boolean
    }
}

fun AppCompatActivity.exitFullscreen() {
    WindowInsetsControllerCompat(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.systemBars())
    }
}


fun AppCompatActivity.setLightStatusBar(enabled: Boolean){
    if (VersionUtils.hasMarshmallow()){
        val decorView = window.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        if (enabled){
            decorView.systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            decorView.systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        }
    }
}

fun AppCompatActivity.setLightNavigationBar(enabled: Boolean) {
    if (VersionUtils.hasOreo()) {
        val decorView = window.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        systemUiVisibility = if (enabled) {
            systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        decorView.systemUiVisibility = systemUiVisibility
    }
}

fun AppCompatActivity.hideStatusBar() {
    hideStatusBar(PreferenceUtil.isFullScreenMode)
}

fun AppCompatActivity.maybeSetScreenOn() {
    if (PreferenceUtil.isScreenOnEnabled) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

fun AppCompatActivity.setEdgeToEdgeOrImmersive() {
    if (PreferenceUtil.isFullScreenMode) {
        setImmersiveFullsreen()
    } else {
        setDrawBehindSystemBar()
    }
}

fun AppCompatActivity.setDrawBehindSystemBar() {
    if (VersionUtils.hasOreo()) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        if (VersionUtils.hasQ()) {
            window.isNavigationBarContrastEnforced = false
        }
    } else {
        setNavigationBarColorPreOreo(surfaceColor())
    }
}

fun AppCompatActivity.setNavigationBarColorPreOreo(color: Int) {
    if (!VersionUtils.hasOreo()) {
        window.navigationBarColor = ColorUtil.darkenColor(color)
    }

}

fun AppCompatActivity.setImmersiveFullsreen() {
    if (PreferenceUtil.isFullScreenMode) {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        if (VersionUtils.hasP()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { v, insets ->
            if (insets.displayCutout != null) {
                insets
            } else {
                // Consume insets if display doesn't have a Cutout
                WindowInsetsCompat.CONSUMED
            }
        }
    }
}


private fun AppCompatActivity.hideStatusBar(fullScreen: Boolean) {
    val statusBar = window.decorView.rootView.findViewById<View>(R.id.status_bar)
}