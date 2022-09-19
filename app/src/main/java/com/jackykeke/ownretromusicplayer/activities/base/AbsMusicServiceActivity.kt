package com.jackykeke.ownretromusicplayer.activities.base

import com.jackykeke.ownretromusicplayer.interfaces.IMusicServiceEventListener
import org.koin.android.ext.android.inject

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsMusicServiceActivity : AbsBaseActivity(), IMusicServiceEventListener {


    private  val mMusicServiceEventListeners = ArrayList<IMusicServiceEventListener>()
    private val repository:RealRepository by inject()


}