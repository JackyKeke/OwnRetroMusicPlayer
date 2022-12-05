package com.jackykeke.ownretromusicplayer.volume

import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler

/**
 *
 * @author keyuliang on 2022/12/2.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AudioVolumeContentObserver internal constructor(
    handler: Handler,
    audioManager: AudioManager,
    audioStreamType: Int,
    listener: OnAudioVolumeChangedListener
) : ContentObserver (handler){

    private val mListener: OnAudioVolumeChangedListener?
    private val mAudioManager: AudioManager?
    private val mAudioStreamType: Int
    private var mLastVolume: Float

    init {
        mAudioManager = audioManager
        mAudioStreamType = audioStreamType
        mListener = listener
        mLastVolume = audioManager.getStreamVolume(mAudioStreamType).toFloat()
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {

        if (mAudioManager !=null&& mListener != null) {
            val maxVolume = mAudioManager.getStreamMaxVolume(mAudioStreamType)
            val currentVolume = mAudioManager.getStreamVolume(mAudioStreamType)
            if (currentVolume.toFloat() != mLastVolume) {
                mLastVolume = currentVolume.toFloat()
                mListener.onAudioVolumeChanged(currentVolume, maxVolume)
            }
        }
    }


}