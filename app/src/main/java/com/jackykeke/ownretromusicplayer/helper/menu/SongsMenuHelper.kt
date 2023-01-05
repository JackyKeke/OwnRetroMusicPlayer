package com.jackykeke.ownretromusicplayer.helper.menu

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.dialog.DeleteSongsDialog
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.RealRepository
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 *
 * @author keyuliang on 2023/1/3.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object SongsMenuHelper :KoinComponent{

    fun handleMenuClick(
        activity: FragmentActivity,
        songs: List<Song>,
        menuItemId: Int
    ):Boolean{
        when (menuItemId) {
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(songs)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(songs)
                return true
            }
            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().fetchPlaylists()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, songs)
                            .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                    }
                }
                return true
            }
            R.id.action_share -> {
                activity.startActivity(
                    Intent.createChooser(
                        MusicUtil.createShareMultipleSongIntent(activity, songs),
                        null
                    )
                )
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(songs)
                    .show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
        }
        return false
    }
}