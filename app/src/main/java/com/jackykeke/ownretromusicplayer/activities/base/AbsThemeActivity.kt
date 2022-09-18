package com.jackykeke.ownretromusicplayer.activities.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.jackykeke.appthemehelper.common.ATHToolbarActivity

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsThemeActivity : ATHToolbarActivity() ,Runnable{

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        updateTheme()
        super.onCreate(savedInstanceState)
    }

    private fun updateTheme() {
        setTheme(getThemeResValue())
    }

}