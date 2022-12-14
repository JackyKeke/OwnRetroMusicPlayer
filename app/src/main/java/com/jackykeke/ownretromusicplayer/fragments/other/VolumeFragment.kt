package com.jackykeke.ownretromusicplayer.fragments.other

import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import com.google.android.material.slider.Slider
import com.jackykeke.appthemehelper.ThemeStore
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.databinding.FragmentVolumeBinding
import com.jackykeke.ownretromusicplayer.extensions.applyColor
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.volume.AudioVolumeObserver
import com.jackykeke.ownretromusicplayer.volume.OnAudioVolumeChangedListener


/**
 * A simple [Fragment] subclass.
 * Use the [VolumeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VolumeFragment : Fragment(), Slider.OnChangeListener, OnAudioVolumeChangedListener,
    View.OnClickListener {

    private var _binding: FragmentVolumeBinding? = null
    private val binding get() = _binding!!

    private var audioVolumeObserver: AudioVolumeObserver? = null

    private val audioManager: AudioManager
        get() = requireContext().getSystemService()!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVolumeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTintable(ThemeStore.accentColor(requireContext()))
        binding.volumeDown.setOnClickListener(this)
        binding.volumeUp.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (audioVolumeObserver== null){
            audioVolumeObserver = AudioVolumeObserver(requireActivity())
        }
        audioVolumeObserver?.register(AudioManager.STREAM_MUSIC,this)

        val audioManager = audioManager
        binding.volumeSeekBar.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.valueFrom = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.addOnChangeListener(this)

    }

    fun setTintable(color: Int) {
        binding.volumeSeekBar.applyColor(color)
    }
    private fun setPauseWhenZeroVolume(pauseWhenZeroVolume: Boolean){
        if (PreferenceUtil.isPauseOnZeroVolume)
            if (MusicPlayerRemote.isPlaying&&pauseWhenZeroVolume)
                MusicPlayerRemote.pauseSong()
    }


    companion object {
        fun newInstance(): VolumeFragment {
            return VolumeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVolumeObserver?.unregister()
        _binding = null
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
         val audioManager = audioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,value.toInt(),0)
        setPauseWhenZeroVolume(value<1f)
        binding.volumeDown.setImageResource(
            if (value == 0f )
                R.drawable.ic_volume_off else R.drawable.ic_volume_down
        )
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        if (_binding!=null){
            binding.volumeSeekBar.valueTo = maxVolume.toFloat()
            binding.volumeSeekBar.value = currentVolume.toFloat()
            binding.volumeDown.setImageResource(if (currentVolume == 0) R.drawable.ic_volume_off else R.drawable.ic_volume_down)

        }
    }


    fun tintWhiteColor() {
        val color = Color.WHITE
        binding.volumeDown.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.volumeUp.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.volumeSeekBar.applyColor(color)
    }


    override fun onClick(v: View ) {

        val audioManager = audioManager
        when(v.id){
            R.id.volumeDown ->audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,0
            )
            R.id.volumeUp -> audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0
            )
        }
    }
}