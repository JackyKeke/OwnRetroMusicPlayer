package com.jackykeke.ownretromusicplayer.service

import android.content.Context
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/9/30.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlaybackManager(val context: Context) {

    var playback:Playback?=null
    private var playbackLocation = PlaybackLocation.LOCAL

    val isLocalPlayback get() = playbackLocation == PlaybackLocation.LOCAL


    val audioSessionId:Int get() = if (playback!=null){
        playback!!.audioSessionId
    }else 0

    val  songDurationMills:Int get() = if (playback!=null){
        playback!!.duration()
    }else -1


    val songProgressMillis: Int
        get() = if (playback != null) {
            playback!!.position()
        } else -1

    val isPlaying: Boolean
        get() = playback != null && playback!!.isPlaying


    init {
        playback = createLocalPlayback()
    }

    private fun createLocalPlayback(): Playback {
        return if (PreferenceUtil.crossFadeDuration == 0){
    }

}

enum class PlaybackLocation {
    LOCAL,
    REMOTE
}