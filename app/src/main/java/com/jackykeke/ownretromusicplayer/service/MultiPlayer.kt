package com.jackykeke.ownretromusicplayer.service

import android.content.Context
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.showToast
import com.jackykeke.ownretromusicplayer.extensions.uri
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.isGapLessPlayback
import com.jackykeke.ownretromusicplayer.util.logE

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class MultiPlayer(context: Context) : LocalPlayback(context) {
    private var mCurrentMediaPlayer = MediaPlayer()
    private var mNextMediaPlayer: MediaPlayer? = null

    override var isInitialized = false
        private set

    override val isPlaying: Boolean
        get() = isInitialized && mCurrentMediaPlayer.isPlaying

    /**
     * Returns the audio session ID.
     *
     * @return The current audio session ID.
     */
    override val audioSessionId: Int
        get() = mCurrentMediaPlayer.audioSessionId

    init {
        mCurrentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
    }

    /**
     * @param song The song object you want to play
     * @return True if the `player` has been prepared and is ready to play, false otherwise
     * 你想要播放的歌曲对象

    @return如果' player '已经准备好并准备好播放，则为True，否则为false
     */
    override fun setDataSource(song: Song, force: Boolean, completion: (success: Boolean) -> Unit) {
        isInitialized = false
        setDataSourceImpl(mCurrentMediaPlayer, song.uri.toString()) { success ->
            isInitialized = success
            if (isInitialized) {
                setNextDataSource(null)
            }
            completion(isInitialized)
        }
    }


    /**
     * Set the MediaPlayer to start when this MediaPlayer finishes playback.
     *
     * @param path The path of the file, or the http/rtsp URL of the stream you want to play
     *
     * 设置MediaPlayer在该MediaPlayer完成播放时启动。

     * @param path 文件的路径，或者你想播放的流的http/rtsp URL
     */
    override fun setNextDataSource(path: String?) {
        try {
            mCurrentMediaPlayer.setNextMediaPlayer(null)
        }catch (e: IllegalArgumentException) {
            Log.i(TAG, "Next media player is current one, continuing")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Media player not initialized!")
            return
        }
        if (mNextMediaPlayer!=null){
            mNextMediaPlayer?.release()
            mNextMediaPlayer=null
        }

        if (path==null)
            return

        if (isGapLessPlayback){
            mNextMediaPlayer = MediaPlayer()
            mNextMediaPlayer?.setWakeMode(context,PowerManager.PARTIAL_WAKE_LOCK)
            mNextMediaPlayer?.audioSessionId = audioSessionId

            setDataSourceImpl(mNextMediaPlayer!!,path){
                success ->
                if (success){
                    try {
                        mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer)
                    }catch (e:IllegalArgumentException){
                        Log.e(TAG, "setNextDataSource: setNextMediaPlayer()", e)
                        if (mNextMediaPlayer != null) {
                            mNextMediaPlayer?.release()
                            mNextMediaPlayer = null
                        }
                    }catch (e: IllegalStateException) {
                        Log.e(TAG, "setNextDataSource: setNextMediaPlayer()", e)
                        if (mNextMediaPlayer != null) {
                            mNextMediaPlayer?.release()
                            mNextMediaPlayer = null
                        }
                    }
                } else {
                    if (mNextMediaPlayer != null) {
                        mNextMediaPlayer?.release()
                        mNextMediaPlayer = null
                    }
                }
            }

        }


    }


    /**
     * Starts or resumes playback.
     */
    override fun start(): Boolean {
        super.start()
        return try {
            mCurrentMediaPlayer.start()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    /**
     * Resets the MediaPlayer to its uninitialized state.
     */
    override fun stop() {
        super.stop()
        mCurrentMediaPlayer.reset()
        isInitialized = false
    }

    /**
     * Releases resources associated with this MediaPlayer object.
     */
    override fun release() {
        stop()
        mCurrentMediaPlayer.release()
        mNextMediaPlayer?.release()
    }

    /**
     * Pauses playback. Call start() to resume.
     */
    override fun pause(): Boolean {
        super.pause()
        return try {
            mCurrentMediaPlayer.pause()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    override var callbacks: Playback.PlaybackCallbacks? = null




    override fun duration(): Int {
        return if (!this.isInitialized){
            -1
        }else try {
            mCurrentMediaPlayer.duration
        }catch (e:IllegalStateException){
            -1
        }
    }

    /**
     * Gets the current playback position.
     *
     * @return The current position in milliseconds
     */
    override fun position(): Int {
        return if (!this.isInitialized){
            -1
        }else try {
            mCurrentMediaPlayer.currentPosition
        }catch (e: IllegalStateException){
            -1
        }
    }

    override fun seek(whereto: Int): Int {
        return try {
            mCurrentMediaPlayer.seekTo(whereto)
            whereto
        } catch (e: IllegalStateException) {
            -1
        }
    }

    override fun setVolume(vol: Float): Boolean {
        return try {
            mCurrentMediaPlayer.setVolume(vol, vol)
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun setAudioSessionId(sessionId: Int): Boolean {
        return try {
            mCurrentMediaPlayer.audioSessionId = sessionId
            true
        } catch (e: IllegalArgumentException) {
            false
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun setCrossFadeDuration(duration: Int) {

    }

    override fun setPlaybackSpeedPitch(speed: Float, pitch: Float) {
        mCurrentMediaPlayer.setPlaybackSpeedPitch(speed, pitch)
        mNextMediaPlayer?.setPlaybackSpeedPitch(speed, pitch)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isInitialized = false
        mCurrentMediaPlayer.release()
        mCurrentMediaPlayer = MediaPlayer()
        mCurrentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        context.showToast(R.string.unplayable_file)
        logE(what.toString() + extra)
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null){
            isInitialized = false
            mCurrentMediaPlayer.release()
            mCurrentMediaPlayer = mNextMediaPlayer!!
            isInitialized = true
            mNextMediaPlayer = null
            callbacks?.onTrackWentToNext()
        }else{
            callbacks?.onTrackEnded()
        }
    }

    companion object {
        val TAG: String = MultiPlayer::class.java.simpleName
    }

}