package com.jackykeke.ownretromusicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.showToast
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.isAudioFocusEnabled
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.playbackPitch
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.playbackSpeed

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class LocalPlayback(val context: Context) : Playback, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private val becomingNoisyReceiverIntentFilter =
        IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private val audioManager: AudioManager? = context.getSystemService()

    private var becomingNoisyReceiverRegistered = false

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null
                && intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY
            ) {
                val serviceIntent = Intent(context, MusicService::class.java)
                serviceIntent.action = MusicService.ACTION_PAUSE
                context.startService(serviceIntent)
            }
        }

        override fun peekService(myContext: Context?, service: Intent?): IBinder {
            return super.peekService(myContext, service)
        }

    }

    private var isPausedByTransientLossOfFocus = false

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!isPlaying && isPausedByTransientLossOfFocus) {
                    start()
                    callbacks?.onPlayStateChanged()
                    isPausedByTransientLossOfFocus = false
                }
                setVolume(Volume.NORMAL)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media playback
                //在无限长的时间内失去焦点:停止播放并释放媒体播放
                if (isAudioFocusEnabled) {
                    pause()
                    callbacks?.onPlayStateChanged()
                }
            }


            // //短暂地失去了注意力，但我们必须停下来回放。我们不释放媒体播放，因为播放可能恢复
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {

                val wasPlaying = isPlaying
                pause()
                callbacks?.onPlayStateChanged()
                isPausedByTransientLossOfFocus = wasPlaying

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                setVolume(Volume.DUCK)
            }

        }
    }

    private val audioFocusRequest:AudioFocusRequestCompat =
        AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .setAudioAttributes(AudioAttributesCompat.Builder().setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC).build())
            .build()

    @CallSuper
    override fun start(): Boolean {
        if (!requestFocus()){
            context.showToast(R.string.audio_focus_denied)
        }
        registerBecomingNoisyReceiver()
        return true
    }

    private fun registerBecomingNoisyReceiver() {
        if (!becomingNoisyReceiverRegistered){
            context.registerReceiver(becomingNoisyReceiver,becomingNoisyReceiverIntentFilter)
            becomingNoisyReceiverRegistered=true
        }

    }

    private fun requestFocus(): Boolean {
        return AudioManagerCompat.requestAudioFocus(audioManager!!,
        audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @CallSuper
    override fun stop() {
        abandonFocus()
        unregisterBecomingNoisyReceiver()
    }

    private fun unregisterBecomingNoisyReceiver() {
         if (becomingNoisyReceiverRegistered){
             context.unregisterReceiver(becomingNoisyReceiver)
             becomingNoisyReceiverRegistered=false
         }
    }

    private fun abandonFocus() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager!!,audioFocusRequest)
    }

    @CallSuper
    override fun pause(): Boolean {
        unregisterBecomingNoisyReceiver()
        return true
    }

    /**
     * @param player The [MediaPlayer] to use
     * @param path The path of the file, or the http/rtsp URL of the stream you want to play
     * @return True if the <code>player</code> has been prepared and is ready to play, false otherwise
     */
    fun setDataSourceImpl(
        player: MediaPlayer,
        path: String,
        completion: (success: Boolean) -> Unit,
    ){
        player.reset()
        try {

            if (path.startsWith("content://")){
                player.setDataSource(context,path.toUri())
            }else{
                player.setDataSource(path)
            }
            player.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())

            if (VersionUtils.hasMarshmallow()){
                player.playbackParams=
                    PlaybackParams().setSpeed(playbackSpeed).setPitch(playbackPitch)
            }

            player.setOnPreparedListener {
                player.setOnPreparedListener(null)
                completion(true)
            }
            player.prepareAsync()

        }catch (e:Exception){
            completion(false)
            e.printStackTrace()
        }
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)

    }


    object Volume {
        /**
         * The volume we set the media player to when we lose audio focus, but are
         * allowed to reduce the volume instead of stopping playback.
         * 当我们失去音频焦点时，我们设置媒体播放器的音量
         *
         * 允许降低音量而不是停止播放。
         */
        const val DUCK = 0.2f

        /** The volume we set the media player when we have audio focus.
         * 当我们有音频对焦时，我们为媒体播放器设置的音量。
         * */
        const val NORMAL = 1.0f
    }
}