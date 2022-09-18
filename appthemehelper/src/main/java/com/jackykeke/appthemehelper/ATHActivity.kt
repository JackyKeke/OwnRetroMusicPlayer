package com.jackykeke.appthemehelper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author keyuliang on 2022/9/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
open class ATHActivity :AppCompatActivity() {


    private var updateTime: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        if (ATH.didThemeValuesChange(this,updateTime)){
            onThemeChanged()
        }
    }

    private fun onThemeChanged() {
        postRecreate()
    }

    private fun postRecreate() {
        // hack to prevent java.lang.RuntimeException: Performing pause of activity that is not resumed
        // makes sure recreate() is called right after and not in onResume()
        Handler(Looper.getMainLooper()).post { recreate() }
    }


}