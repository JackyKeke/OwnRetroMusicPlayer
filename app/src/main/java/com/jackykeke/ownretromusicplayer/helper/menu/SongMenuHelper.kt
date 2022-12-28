package com.jackykeke.ownretromusicplayer.helper.menu

import android.media.RingtoneManager
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.model.Song
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent

/**
 *
 * @author keyuliang on 2022/12/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object SongMenuHelper :KoinComponent {


    const val MENU_RES = R.menu.menu_item_song

    fun handleMenuClick(activity:FragmentActivity,song:Song,menuItemId:Int):Boolean{
        val  libraryViewModel = activity.getViewModel() as LibraryViewModel
        when (menuItemId){
            R.id.action_set_as_ringtone ->{
                if (RingtoneManager)
            }
        }
    }

}