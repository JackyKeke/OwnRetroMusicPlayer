package com.jackykeke.ownretromusicplayer.volume

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.getSystemService

/**
 *
 * @author keyuliang on 2022/12/1.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AudioVolumeObserver(private val context:Context) {


    private val mAudioManager: AudioManager =
        context.getSystemService()!!
    private var contentObserver: AudioVolumeContentObserver? = null


    fun register(audioStreamType: Int, listener: OnAudioVolumeChangedListener){
        val handler = Handler(Looper.getMainLooper())
        contentObserver = AudioVolumeContentObserver(
            handler,
            mAudioManager,
            audioStreamType,
            listener
        )

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            contentObserver!!
        )
    }

    fun unregister(){
        if (contentObserver != null) {
            context.contentResolver.unregisterContentObserver(contentObserver!!)
            contentObserver = null
        }
    }

}