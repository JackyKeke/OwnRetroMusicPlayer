package com.jackykeke.ownretromusicplayer.extensions

import android.app.ActivityManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
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


fun AppCompatActivity.setTaskDescriptionColorAuto(){
    setTaskDescriptionColor(surfaceColor())
}

fun FragmentActivity.setTaskDescriptionColor(color: Int) {

    var colorFinal = color
    // Task description requires fully opaque color
    colorFinal = ColorUtil.stripAlpha(colorFinal)
    // Sets color of entry in the system recents page
    if (VersionUtils.hasP()){
        setTaskDescription(
            ActivityManager.TaskDescription(title as String? , -1 , colorFinal)
        )
    }else {
        setTaskDescription(ActivityManager.TaskDescription(title as String?))
    }

}


fun AppCompatActivity.hideStatusBar(){
    hideStatusBar(PreferenceUtil.isFullScreenMode)
}


private fun AppCompatActivity.hideStatusBar(fullScreen:Boolean){
    val statusBar = window.decorView.rootView.findViewById<View>(R.id.status_bar)
}