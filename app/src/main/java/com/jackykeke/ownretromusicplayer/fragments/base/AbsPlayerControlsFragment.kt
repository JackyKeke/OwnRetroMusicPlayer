package com.jackykeke.ownretromusicplayer.fragments.base

import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.slider.Slider
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.whichFragment
import com.jackykeke.ownretromusicplayer.fragments.MusicSeekSkipTouchListener
import com.jackykeke.ownretromusicplayer.fragments.other.VolumeFragment
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.helper.MusicProgressViewUpdateHelper
import com.jackykeke.ownretromusicplayer.service.MusicService
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.util.color.MediaNotificationProcessor

/**
 *
 * @author keyuliang on 2022/12/9.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsPlayerControlsFragment(@LayoutRes layout: Int) :
    AbsMusicServiceFragment(layout), MusicProgressViewUpdateHelper.Callback {


    protected abstract fun show()

    protected abstract fun hide()

    abstract fun setColor(color: MediaNotificationProcessor)

    var lastPlaybackControlsColor: Int = 0

    var lastDisabledPlaybackControlsColor: Int = 0

    private var isSeeking = false

    open val progressSlider: Slider? = null

    open val seekBar: SeekBar? = null

    abstract val shuffleButton: ImageButton

    abstract val repeatButton: ImageButton

    open val nextButton: ImageButton? = null

    open val previousButton: ImageButton? = null

    open val songTotalTime: TextView? = null

    open val songCurrentProgress: TextView? = null

    private var progressAnimator: ObjectAnimator? = null

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        if (seekBar == null) {
            progressSlider?.valueTo = total.toFloat()
            progressSlider?.value =
                progress.toFloat().coerceIn(progressSlider?.valueFrom, progressSlider?.valueTo)

        } else {
            seekBar?.max = total
            if (isSeeking) {
                seekBar?.progress = progress
            } else {
                progressAnimator = ObjectAnimator.ofInt(seekBar, "progress", progress).apply {
                    duration = SLIDER_ANIMATION_TIME
                    interpolator = LinearInterpolator()
                    start()
                }
            }
        }
        songTotalTime?.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress?.text = MusicUtil.getReadableDurationString(progress.toLong())

    }


    private fun setUpProgressSlider() {
        progressSlider?.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                onProgressChange(value.toInt(), fromUser)
            }
        })

        progressSlider?.addOnSliderTouchListener(object :Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
                 onStartTrackingTouch()
            }

            override fun onStopTrackingTouch(slider: Slider) {
                 onStopTrackingTouch(slider)
            }

        })
        seekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                 onProgressChange(progress,fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                 onStartTrackingTouch()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                 onStopTrackingTouch(seekBar)
            }
        })
    }


    private fun onProgressChange(value: Int, fromUser: Boolean) {
        if (fromUser) {
            onUpdateProgressViews(value, MusicPlayerRemote.songDurationMillis)
        }
    }

    private fun onStartTrackingTouch() {
        isSeeking = true
        progressViewUpdateHelper.stop()
        progressAnimator?.cancel()
    }

    private fun onStopTrackingTouch(value: Int) {
        isSeeking = false
        MusicPlayerRemote.seekTo(value)
        progressViewUpdateHelper.start()

    }

    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    fun View.showBounceAnimation() {
        clearAnimation()
        scaleX = 0.9f
        scaleY = 0.9f
        isVisible = true
        pivotX = (width / 2).toFloat()
        pivotY = (height / 2).toFloat()

        animate().setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .scaleX(1.1f)
            .scaleY(1.1f)
            .withEndAction {
                animate().setDuration(200)
                    .setInterpolator(AccelerateInterpolator())
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .start()
            }
            .start()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideVolumeIfAvailable()
    }

    protected var volumeFragment: VolumeFragment? = null


    private fun hideVolumeIfAvailable() {
        if (PreferenceUtil.isVolumeVisibilityMode) {
            childFragmentManager.commit {
                replace<VolumeFragment>(R.id.volumeFragmentContainer)
            }
            childFragmentManager.executePendingTransactions()
        }
        volumeFragment = whichFragment(R.id.volumeFragmentContainer)
    }

    override fun onStart() {
        super.onStart()
        setUpProgressSlider()
        setUpPrevNext()
        setUpShuffleButton()
        setUpRepeatButton()
    }

    private fun setUpPrevNext() {
        nextButton?.setOnTouchListener(MusicSeekSkipTouchListener(requireActivity(), true))
        previousButton?.setOnTouchListener(MusicSeekSkipTouchListener(requireActivity(), true))

    }

    private fun setUpShuffleButton() {
        shuffleButton.setOnClickListener { MusicPlayerRemote.toggleShuffleMode() }

    }

    private fun setUpRepeatButton() {
        repeatButton.setOnClickListener { MusicPlayerRemote.cycleRepeatMode() }

    }


    fun updatePrevNextColor(){
        nextButton?.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        previousButton?.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
    }


    fun updateShuffleState(){
        shuffleButton.setColorFilter(
            when(MusicPlayerRemote.shuffleMode){
                MusicService.SHUFFLE_MODE_SHUFFLE -> lastPlaybackControlsColor
                else -> lastDisabledPlaybackControlsColor
            },PorterDuff.Mode.SRC_IN
        )
    }

    fun updateRepeatState(){
        when(MusicPlayerRemote.repeatMode){
            MusicService.REPEAT_MODE_NONE ->{
                repeatButton.setImageResource(R.drawable.ic_repeat)
                repeatButton.setColorFilter(
                    lastDisabledPlaybackControlsColor,
                    PorterDuff.Mode.SRC_IN
                )
            }

            MusicService.REPEAT_MODE_ALL ->{
                repeatButton.setImageResource(R.drawable.ic_repeat)
                repeatButton.setColorFilter(
                    lastPlaybackControlsColor,
                    PorterDuff.Mode.SRC_IN
                )
            }
            MusicService.REPEAT_MODE_THIS -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_one)
                repeatButton.setColorFilter(
                    lastPlaybackControlsColor,
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }


    companion object {
        const val SLIDER_ANIMATION_TIME: Long = 400
    }

}