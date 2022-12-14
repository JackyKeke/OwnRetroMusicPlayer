package com.jackykeke.ownretromusicplayer.activities

import android.app.KeyguardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.base.AbsMusicServiceActivity
import com.jackykeke.ownretromusicplayer.databinding.ActivityLockScreenBinding
import com.jackykeke.ownretromusicplayer.extensions.hideStatusBar
import com.jackykeke.ownretromusicplayer.extensions.setTaskDescriptionColorAuto
import com.jackykeke.ownretromusicplayer.extensions.whichFragment
import com.jackykeke.ownretromusicplayer.fragments.base.AbsMusicServiceFragment
import com.jackykeke.ownretromusicplayer.fragments.lockscreen.LockScreenControlsFragment
import com.jackykeke.ownretromusicplayer.glide.GlideApp
import com.jackykeke.ownretromusicplayer.glide.RetroGlideExtension
import com.jackykeke.ownretromusicplayer.glide.RetroMusicColoredTarget
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.util.color.MediaNotificationProcessor
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import java.util.concurrent.locks.Lock

class LockScreenActivity : AbsMusicServiceActivity() {

    private lateinit var binding: ActivityLockScreenBinding
    private var fragment: LockScreenControlsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockScreenInit()
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        setTaskDescriptionColorAuto()

        val config = SlidrConfig.Builder().listener(object :SlidrListener{
            override fun onSlideStateChanged(state: Int) {
                TODO("Not yet implemented")
            }

            override fun onSlideChange(percent: Float) {
                TODO("Not yet implemented")
            }

            override fun onSlideOpened() {
                TODO("Not yet implemented")
            }

            override fun onSlideClosed(): Boolean {
                if (VersionUtils.hasOreo()) {
                    val keyguardManager =
                        getSystemService<KeyguardManager>()
                    keyguardManager?.requestDismissKeyguard(this@LockScreenActivity, null)
                }
                finish()
                return true
            }

        }).position(SlidrPosition.BOTTOM).build()

        Slidr.attach(this,config)

        fragment = whichFragment<LockScreenControlsFragment>(R.id.playback_controls_fragment)

        binding.slide.apply {
            translationY = 100f
            alpha = 0f
            animate().translationY(0f).alpha(1f).setDuration(1500).start()
        }

    }



    private fun lockScreenInit() {
        if (VersionUtils.hasOreoMR1()) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            //          or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON

        }

    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSongs()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSongs()
    }


    private fun updateSongs() {
        val song = MusicPlayerRemote.currentSong
        GlideApp.with(this)
            .asBitmapPalette()
            .songCoverOptions(song)
            .load(RetroGlideExtension.getSongModel(song))
            .dontAnimate()
            .into(object : RetroMusicColoredTarget(binding.image) {
                override fun onColorReady(colors: MediaNotificationProcessor) {
                    fragment?.setColor(colors)
                }
            })

    }

}