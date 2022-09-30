package com.jackykeke.ownretromusicplayer.service.playback

import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/9/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
interface Playback {

    val isInitialized : Boolean

    val isPlaying:Boolean

    val audioSessionId:Int

    fun setDataSource(
        song: Song,
        force:Boolean,
        completion:(success:Boolean) -> Unit
    )

    fun setNextDataSource(path:String?)

    var callbacks: PlaybackCallbacks?

    fun start():Boolean

    fun stop()

    fun release()

    fun pause():Boolean

    fun duration(): Int

    fun position(): Int

    fun seek(whereto: Int): Int

    fun setVolume(vol: Float): Boolean

    fun setAudioSessionId(sessionId: Int): Boolean

    fun setCrossFadeDuration(duration: Int)

    fun setPlaybackSpeedPitch(speed: Float, pitch: Float)

    interface PlaybackCallbacks {
        fun onTrackWentToNext()

        fun onTrackEnded()

        fun onTrackEndedWithCrossfade()

        fun onPlayStateChanged()
    }
}