package com.jackykeke.ownretromusicplayer.activities

import android.content.SharedPreferences
import android.os.Bundle
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.base.AbsCastActivity
import com.jackykeke.ownretromusicplayer.extensions.setTaskDescriptionColorAuto

class MainActivity : AbsCastActivity() {

    companion object{
        const val TAG = "MainActivity"
        const val EXPAND_PANEL = "expand_panel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTaskDescriptionColorAuto()
        
    }


    override fun onServiceConnected() {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onQueueChanged() {
        TODO("Not yet implemented")
    }

    override fun onFavoriteStateChanged() {
        TODO("Not yet implemented")
    }

    override fun onPlayingMetaChanged() {
        TODO("Not yet implemented")
    }

    override fun onPlayStateChanged() {
        TODO("Not yet implemented")
    }

    override fun onRepeatModeChanged() {
        TODO("Not yet implemented")
    }

    override fun onShuttleModeChanged() {
        TODO("Not yet implemented")
    }

    override fun onMediaStoreChanged() {
        TODO("Not yet implemented")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
    }
}