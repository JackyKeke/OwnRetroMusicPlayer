package com.jackykeke.ownretromusicplayer.activities.base

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.os.ConfigurationCompat
import code.name.monkey.retromusic.extensions.installSplitCompat
import com.jackykeke.appthemehelper.common.ATHToolbarActivity
import com.jackykeke.ownretromusicplayer.LanguageContextWrapper
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.*
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.util.maybeShowAnnoyingToasts
import com.jackykeke.ownretromusicplayer.util.theme.getNightMode
import com.jackykeke.ownretromusicplayer.util.theme.getThemeResValue
import java.util.*

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
        hideStatusBar()

        super.onCreate(savedInstanceState)

        setEdgeToEdgeOrImmersive()
        maybeSetScreenOn()
        setLightNavigationBarAuto()
        setLightStatusBarAuto(surfaceColor())
        setForceDarkAllowed(false)
        maybeShowAnnoyingToasts()
    }

    private fun updateTheme() {
        setTheme(getThemeResValue())
        if (PreferenceUtil.materialYou){
            setDefaultNightMode(getNightMode())
        }


        if(PreferenceUtil.isCustomFont){
            setTheme(R.style.FontThemeOverlay)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            hideStatusBar()
            handler.removeCallbacks(this)
            handler.postDelayed(this,300)
        }else{
            handler.removeCallbacks(this)
        }
    }

    override fun run() {
         setImmersiveFullsreen()
    }

    override fun onStop() {
        handler.removeCallbacks(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitFullscreen()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            handler.removeCallbacks(this)
            handler.postDelayed(this,500)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun attachBaseContext(newBase: Context?) {
        val code = PreferenceUtil.languageCode
        val locale = if (code == "auto"){
            ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        }else {
            Locale.forLanguageTag(code)
        }
        super.attachBaseContext(LanguageContextWrapper.wrap(newBase,locale))
        installSplitCompat()
    }

}