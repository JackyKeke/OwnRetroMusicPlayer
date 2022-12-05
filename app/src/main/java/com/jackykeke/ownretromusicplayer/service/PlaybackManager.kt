package com.jackykeke.ownretromusicplayer.service

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import com.jackykeke.ownretromusicplayer.model.Song
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

    var playback: Playback? = null
    private var playbackLocation = PlaybackLocation.LOCAL

    val isLocalPlayback get() = playbackLocation == PlaybackLocation.LOCAL


    val audioSessionId: Int
        get() = if (playback != null) {
            playback!!.audioSessionId
        } else 0

    val songDurationMills: Int
        get() = if (playback != null) {
            playback!!.duration()
        } else -1


    val songProgressMillis: Int
        get() = if (playback != null) {
            playback!!.position()
        } else -1

    val songDurationMillis: Int
        get() = if (playback != null) {
            playback!!.duration()
        } else -1


    val isPlaying: Boolean
        get() = playback != null && playback!!.isPlaying


    init {
        playback = createLocalPlayback()
    }

    private fun createLocalPlayback(): Playback {
        return if (PreferenceUtil.crossFadeDuration == 0) {
            MultiPlayer(context)
        } else {
            CrossFadePlayer(context)
        }
    }

    fun pause(force: Boolean, onPause: () -> Unit) {
        if (playback != null && playback!!.isPlaying) {
            if (force) {
                playback?.pause()
                closeAudioEffectSession()
                onPause()
            } else {
                AudioFader.startFadeAnimator(playback!!, false) {
                    //Code to run when Animator Ends
                    playback?.pause()
                    closeAudioEffectSession()
                    onPause()
                }
            }
        }
    }

    fun seek(millis: Int): Int = playback!!.seek(millis)


    fun setNextDataSource(trackUri: String?) {
        playback?.setNextDataSource(trackUri)
    }




    /**
     * @param crossFadeDuration CrossFade duration
     * @return Whether switched playback
     */
    fun maybeSwitchToCrossFade(crossFadeDuration: Int): Boolean {
        /* Switch to MultiPlayer if CrossFade duration is 0 and
                Playback is not an instance of MultiPlayer */
        if (playback !is MultiPlayer && crossFadeDuration == 0) {
            if (playback != null) {
                playback?.release()
            }
            playback = null
            playback = MultiPlayer(context)
            return true
        } else if (playback !is CrossFadePlayer && crossFadeDuration > 0) {
            if (playback != null) {
                playback?.release()
            }
            playback = null
            playback = CrossFadePlayer(context)
            return true
        }
        return false
    }

    fun release() {
        playback?.release()
        playback = null
        closeAudioEffectSession()
    }

    fun play(onNotInitialized: () -> Unit) {
        if (playback != null && !playback!!.isPlaying) {
            if (!playback!!.isInitialized) {
                onNotInitialized()
            } else {
                openAudioEffectSession()
                if (playbackLocation == PlaybackLocation.LOCAL) {
                    if (playback is CrossFadePlayer) {
                        if (!(playback as CrossFadePlayer).isCrossFading) {
                            AudioFader.startFadeAnimator(playback!!, true)
                        }

                    } else {
                        AudioFader.startFadeAnimator(playback!!, true)
                    }
                }
                playback?.start()
            }


        }
    }

    private fun openAudioEffectSession() {
        val intent = Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        context.sendBroadcast(intent)
    }

    private fun closeAudioEffectSession() {
        val audioEffectsIntent = Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        if (playback != null) {
            audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION,
                playback!!.audioSessionId)
        }
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        context.sendBroadcast(audioEffectsIntent)
    }


    fun setPlaybackSpeedPitch(playbackSpeed: Float, playbackPitch: Float) {
        playback?.setPlaybackSpeedPitch(playbackSpeed, playbackPitch)
    }


    fun setDataSource(song: Song, force: Boolean, completion: (success: Boolean) -> Unit) {
        playback?.setDataSource(song, force, completion)
    }

    fun switchToRemotePlayback(
        castPlayer: CastPlayer,
        onChange: (wasPlaying: Boolean, progress: Int) -> Unit,
    ) {
        playbackLocation = PlaybackLocation.REMOTE
        switchToPlayback(castPlayer, onChange)
    }


    fun switchToLocalPlayback(onChange: (wasPlaying: Boolean, progress: Int) -> Unit) {
        playbackLocation = PlaybackLocation.LOCAL
        switchToPlayback(createLocalPlayback(), onChange)
    }

    private fun switchToPlayback(
        playback: Playback,
        onChange: (wasPlaying: Boolean, progress: Int) -> Unit,
    ) {
        val oldPlayback = this.playback
        val wasPlaying = oldPlayback?.isPlaying == true
        val progress: Int = oldPlayback?.position() ?: 0
        this.playback = playback
        oldPlayback?.stop()
        onChange(wasPlaying, progress)
    }

    fun setCallbacks(callbacks: Playback.PlaybackCallbacks) {
        playback?.callbacks = callbacks
    }

    fun setCrossFadeDuration(duration: Int) {
        playback?.setCrossFadeDuration(duration)
    }

}

enum class PlaybackLocation {
    LOCAL,
    REMOTE
}