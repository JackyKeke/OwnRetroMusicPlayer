package com.jackykeke.ownretromusicplayer.helper.menu

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.RingtoneManager
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
                if (RingtoneManager.requiresDialog(activity)){
                    RingtoneManager.showDialog(activity)
                }else{
                    RingtoneManager.setRingtone(activity,song)
                }
                return true
            }

            R.id.action_share -> {
                //用于创建ACTION_CHOOSER Intent 的便捷函数。
                //构建一个包装给定目标意图的新ACTION_CHOOSER意图，还可以选择提供标题。
                // 如果目标意图指定FLAG_GRANT_READ_URI_PERMISSION或FLAG_GRANT_WRITE_URI_PERMISSION ，
                // 那么这些标志也将在返回的选择器意图中设置，并适当设置其 ClipData：如果它不为空，则直接反映getClipData() ，或者从中构建新的 ClipData getData() 。
                //参数：
                //target – 用户将选择要执行的活动的 Intent。
                //title – 可选标题，仅当目标操作不是 ACTION_SEND 或 ACTION_SEND_MULTIPLE 时才会显示在选择器中。
                //退货：
                //返回一个新的 Intent 对象，您可以将其传递给Context.startActivity()和相关方法。
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(activity,song),null))
                return true
            }

            R.id.action_delete_from_device ->{
                DeleteSongsDialog.create(song).show(activity.supportFragmentManager,"")
            }

        }
    }

}