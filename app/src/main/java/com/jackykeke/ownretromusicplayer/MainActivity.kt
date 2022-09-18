package com.jackykeke.ownretromusicplayer

import android.os.Bundle
import com.jackykeke.ownretromusicplayer.activities.base.AbsCastActivity

class MainActivity : AbsCastActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
    }
}