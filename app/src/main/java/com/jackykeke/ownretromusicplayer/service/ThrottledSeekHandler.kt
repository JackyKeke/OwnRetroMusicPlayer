package com.jackykeke.ownretromusicplayer.service

import android.os.Handler
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.PLAY_STATE_CHANGED

/**
 *
 * @author keyuliang on 2022/10/9.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class ThrottledSeekHandler (
    private val musicService: MusicService,
    private val handler: Handler
        ) :Runnable{

    fun notifySeek() {
        musicService.updateMediaSessionPlaybackState()
        handler.removeCallbacks(this)
        handler.postDelayed(this, THROTTLE)
    }

    override fun run() {
        musicService.savePositionInTrack()
        musicService.sendPublicIntent(PLAY_STATE_CHANGED) // for musixmatch synced lyrics

    }


    companion object {
        // milliseconds to throttle before calling run() to aggregate events
        private const val THROTTLE: Long = 500
    }
}