package com.jackykeke.ownretromusicplayer.fragments.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.base.AbsMusicServiceActivity
import com.jackykeke.ownretromusicplayer.interfaces.IMusicServiceEventListener

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
open class AbsMusicServiceFragment(@LayoutRes layout: Int)  :Fragment(layout),
    IMusicServiceEventListener {


        val navOptions by lazy {
           navOptions {
               launchSingleTop=false
               anim {
                   enter  = R.anim.retro_fragment_open_enter
                   exit = R.anim.retro_fragment_open_exit
                   popEnter = R.anim.retro_fragment_close_enter
                   popExit = R.anim.retro_fragment_close_exit

               }

           }
        }

    var serviceActivity:AbsMusicServiceActivity?=null
            private set

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            serviceActivity  = context as AbsMusicServiceActivity?
        }catch (e:ClassCastException){
            throw RuntimeException(context.javaClass.simpleName + " must  be an instance  of  "+ AbsMusicServiceActivity::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        serviceActivity=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceActivity?.addMusicServiceEventListener(this)
    }


}