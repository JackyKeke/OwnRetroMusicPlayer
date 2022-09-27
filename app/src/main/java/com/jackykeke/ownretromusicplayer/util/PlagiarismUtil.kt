package com.jackykeke.ownretromusicplayer.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.jackykeke.ownretromusicplayer.BuildConfig
import com.jackykeke.ownretromusicplayer.extensions.showToast

/**
 *
 * @author keyuliang on 2022/9/27.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun Activity.maybeShowAnnoyingToasts(){

    if (BuildConfig.APPLICATION_ID != "com.jackykeke.ownretromusicplayer" &&
        BuildConfig.APPLICATION_ID != "com.jackykeke.ownretromusicplayer.debug"&&
        BuildConfig.APPLICATION_ID != "com.jackykeke.ownretromusicplayer.normal"){

        if (BuildConfig.DEBUG) {
            // Log these things to console, if the plagiarizer even cares to check it
            Log.d("Retro Music", "What are you doing with your life?")
            Log.d("Retro Music", "Stop copying apps and make use of your brain.")
            Log.d("Retro Music", "Stop doing this or you will end up straight to hell.")
            Log.d("Retro Music", "To the boiler room of hell. All the way down.")
        } else {
            showToast("Warning! This is a copy of Retro Music Player", Toast.LENGTH_LONG)
            showToast("Instead of using this copy by a dumb person who didn't even bother to remove this code.", Toast.LENGTH_LONG)
            showToast("Support us by downloading the original version from Play Store.", Toast.LENGTH_LONG)
            val packageName = "code.name.monkey.retromusic"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()))
            }
        }

    }
}