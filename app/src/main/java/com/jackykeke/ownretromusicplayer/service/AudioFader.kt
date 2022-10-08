package com.jackykeke.ownretromusicplayer.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import androidx.core.animation.doOnEnd
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AudioFader {

    companion object{
        fun createFadeAnimator(
            fadeInMp: MediaPlayer,
            fadeOutMp:MediaPlayer,
            endAction: (animator: Animator) -> Unit, /* Code to run when Animator Ends*/
        ):Animator?{
            val duration = PreferenceUtil.crossFadeDuration * 1000

            if (duration==0) return null

            return ValueAnimator.ofFloat(0f,1f).apply {
                this.duration = duration.toLong()
                addUpdateListener { anim:ValueAnimator->
                    fadeInMp.setVolume(anim.animatedValue as Float, anim.animatedValue as Float)

                    fadeOutMp.setVolume(1 - anim.animatedValue as Float,
                        1 - anim.animatedValue as Float)
                    doOnEnd {
                        endAction(it)
                    }
                }
            }
        }

        fun startFadeAnimator(
            playback: Playback,
            fadeIn: Boolean, /* fadeIn -> true  fadeOut -> false*/
            callback: Runnable? = null, /* Code to run when Animator Ends*/
        ) {
            val duration = PreferenceUtil.audioFadeDuration.toLong()
            if (duration == 0L) {
                callback?.run()
                return
            }
            val startValue = if (fadeIn) 0f else 1.0f
            val endValue = if (fadeIn) 1.0f else 0f
            val animator = ValueAnimator.ofFloat(startValue, endValue)
            animator.duration = duration
            animator.addUpdateListener { animation: ValueAnimator ->
                playback.setVolume(animation.animatedValue as Float)
            }
            animator.doOnEnd {
                callback?.run()
            }
            animator.start()
        }

    }
}