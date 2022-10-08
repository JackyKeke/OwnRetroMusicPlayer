package com.jackykeke.ownretromusicplayer.service

import android.animation.Animator
import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.PowerManager
import com.jackykeke.appthemehelper.util.VersionUtils.hasMarshmallow
import com.jackykeke.ownretromusicplayer.extensions.uri
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.service.AudioFader.Companion.createFadeAnimator
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.crossFadeDuration
import kotlinx.coroutines.*
import java.time.Duration

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class CrossFadePlayer (context: Context) : LocalPlayback(context){

    private var currentPlayer:CurrentPlayer = CurrentPlayer.NOT_SET
    private var crossFadeDuration = PreferenceUtil.crossFadeDuration

    private var player1 = MediaPlayer()
    private var player2 = MediaPlayer()
    private var mIsInitialized = false
    private var hasDataSource: Boolean = false /* Whether first player has DataSource */
    private var crossFadeAnimator: Animator? = null
    override var callbacks: Playback.PlaybackCallbacks? = null

    private var durationListener = DurationListener()
    var isCrossFading = false

    init {
        player1.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        player2.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        currentPlayer = CurrentPlayer.PLAYER_ONE
    }

    inner class DurationListener :CoroutineScope by crossFadeScope(){

        private var job:Job?=null

        fun start(){
            job?.start()
            job = launch {
                while (true){
                    delay(250)
                    onDurationUpdated(position(),duration())
                }
            }
        }

        fun stop(){
            job?.cancel()
        }
    }

    private fun onDurationUpdated(progress: Int, total: Int) {
        if (total>0  && (total-progress).div(1000) == crossFadeDuration){
            getNextPlayer()?.let{ player->
                val nextSong = MusicPlayerRemote.nextSong
                // Switch to other player (Crossfade) only if next song exists
                if (nextSong != null){
                    setDataSourceImpl(player , nextSong.uri.toString()){
                        success ->
                        if (success) switchPlayer()
                    }
                }
            }
        }
    }

    private fun switchPlayer() {
        getNextPlayer()?.start()
        crossFade(getNextPlayer()!!,getCurrentPlayer()!!)
        currentPlayer=
            if (currentPlayer == CurrentPlayer.PLAYER_ONE || currentPlayer == CurrentPlayer.NOT_SET){
                CurrentPlayer.PLAYER_TWO
            }else{
                CurrentPlayer.PLAYER_ONE
            }
        callbacks?.onTrackEndedWithCrossfade()
    }


    private fun crossFade(fadeInMp: MediaPlayer, fadeOutMp: MediaPlayer) {
        isCrossFading = true
        crossFadeAnimator = createFadeAnimator(fadeInMp,fadeOutMp){
            crossFadeAnimator = null
            durationListener.start()
            isCrossFading=false
        }
        crossFadeAnimator?.start()
    }

    private fun getCurrentPlayer(): MediaPlayer? {
        return when (currentPlayer) {
            CurrentPlayer.PLAYER_ONE -> {
                player1
            }
            CurrentPlayer.PLAYER_TWO -> {
                player2
            }
            CurrentPlayer.NOT_SET -> {
                null
            }
        }
    }

    private fun getNextPlayer(): MediaPlayer? {
        return when (currentPlayer) {
            CurrentPlayer.PLAYER_ONE -> {
                player2
            }
            CurrentPlayer.PLAYER_TWO -> {
                player1
            }
            CurrentPlayer.NOT_SET -> {
                null
            }
        }
    }

    override val isInitialized: Boolean
        get() = mIsInitialized
    override val isPlaying: Boolean
        get() =  mIsInitialized && getCurrentPlayer()?.isPlaying == true
    override val audioSessionId: Int
        get() = getCurrentPlayer()?.audioSessionId!!

    enum class CurrentPlayer {
        PLAYER_ONE,
        PLAYER_TWO,
        NOT_SET
    }

    override fun setDataSource(song: Song, force: Boolean, completion: (success: Boolean) -> Unit) {
         if (force) hasDataSource = false

        mIsInitialized = false

        /* We've already set DataSource if initialized is true in setNextDataSource */
        if (!hasDataSource){
            getCurrentPlayer()?.let {
                setDataSourceImpl(it,song.uri.toString()){
                    success ->
                    mIsInitialized = success
                    completion(success)
                }
            }
            hasDataSource = true
        }else {
            completion(true)
            mIsInitialized = true
        }

    }

    override fun setNextDataSource(path: String?) {

    }


    override fun release() {
        stop()
        cancelFade()
        getCurrentPlayer()?.release()
        getNextPlayer()?.release()
        durationListener.cancel()
    }

    private fun cancelFade() {
        crossFadeAnimator?.cancel()
        crossFadeAnimator = null
    }

    override fun duration(): Int {
        return if (!mIsInitialized) {
            -1
        } else try {
            getCurrentPlayer()?.duration!!
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            -1
        }
    }

    override fun position(): Int {
        TODO("Not yet implemented")
    }

    override fun seek(whereto: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVolume(vol: Float): Boolean {
        TODO("Not yet implemented")
    }

    override fun setAudioSessionId(sessionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun setCrossFadeDuration(duration: Int) {
        crossFadeDuration = duration
    }

    override fun setPlaybackSpeedPitch(speed: Float, pitch: Float) {
       getCurrentPlayer()?.setPlaybackSpeedPitch(speed, pitch)
        if (getNextPlayer()?.isPlaying==true){
            getCurrentPlayer()?.setPlaybackSpeedPitch(speed, pitch)
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }

}

internal fun crossFadeScope(): CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)


fun MediaPlayer.setPlaybackSpeedPitch(speed: Float, pitch: Float) {
    if (hasMarshmallow()) {
        val wasPlaying = isPlaying
        playbackParams = PlaybackParams().setSpeed(speed).setPitch(pitch)
        if (!wasPlaying) {
            pause()
        }
    }
}