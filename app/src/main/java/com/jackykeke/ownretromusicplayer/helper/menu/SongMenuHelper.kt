package com.jackykeke.ownretromusicplayer.helper.menu

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.jackykeke.ownretromusicplayer.EXTRA_ALBUM_ID
import com.jackykeke.ownretromusicplayer.EXTRA_ARTIST_ID
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.dialog.DeleteSongsDialog
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.fragments.ReloadType
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.providers.BlacklistStore
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.RingtoneManager
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent
import java.io.File

/**
 *
 * @author keyuliang on 2022/12/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object SongMenuHelper : KoinComponent {


    const val MENU_RES = R.menu.menu_item_song

    fun handleMenuClick(activity: FragmentActivity, song: Song, menuItemId: Int): Boolean {
        val libraryViewModel = activity.getViewModel() as LibraryViewModel
        when (menuItemId) {
            R.id.action_set_as_ringtone -> {
                if (RingtoneManager.requiresDialog(activity)) {
                    RingtoneManager.showDialog(activity)
                } else {
                    RingtoneManager.setRingtone(activity, song)
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
                activity.startActivity(
                    Intent.createChooser(
                        MusicUtil.createShareSongFileIntent(
                            activity,
                            song
                        ), null
                    )
                )
                return true
            }

            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(song).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }

            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(song)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(song)
                return true
            }
            R.id.action_tag_editor -> {
                val tagEditorIntent = Intent(activity, SongTagEditorActivity::class.java)
                tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
                if (activity is IPaletteColorHolder)
                    tagEditorIntent.putExtra(
                        AbsTagEditorActivity.EXTRA_PALETTE,
                        (activity as IPaletteColorHolder).paletteColor
                    )
                activity.startActivity(tagEditorIntent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(song).show(activity.supportFragmentManager, "SONG_DETAILS")
                return true
            }
            R.id.action_go_to_album -> {
                activity.findNavController(R.id.fragment_container).navigate(
                    R.id.albumDetailsFragment,
                    bundleOf(EXTRA_ALBUM_ID to song.albumId)
                )
                return true
            }
            R.id.action_go_to_artist -> {
                activity.findNavController(R.id.fragment_container).navigate(
                    R.id.artistDetailsFragment,
                    bundleOf(EXTRA_ARTIST_ID to song.artistId)
                )
                return true
            }
            R.id.action_add_to_blacklist -> {
                BlacklistStore.getInstance(activity).addPath(File(song.data))
                libraryViewModel.forceReload(ReloadType.Songs)
                return true
            }
        }
        return false
    }

    abstract class OnClickSongMenu(private val activity: FragmentActivity) :
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        open val menuRes: Int
            get() = MENU_RES

        abstract val song:Song

        override fun onClick(v: View ) {
             val popupMenu = PopupMenu(activity,v)
            popupMenu.inflate(menuRes)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }


        override fun onMenuItemClick(item: MenuItem): Boolean {
            return handleMenuClick(activity,song,item.itemId)
        }
    }

}