package com.jackykeke.ownretromusicplayer.helper.menu

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.dialog.AddToPlaylistDialog
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Genre
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.GenreRepository
import com.jackykeke.ownretromusicplayer.repository.RealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 *
 * @author keyuliang on 2023/1/3.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object GenreMenuHelper :KoinComponent {

    private val genreRepository by inject<GenreRepository>()

    fun handleMenuClick(activity: FragmentActivity,genre:Genre ,item:MenuItem):Boolean{
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(getGenreSongs(genre), 0, true)
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(getGenreSongs(genre))
                return true
            }
            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().fetchPlaylists()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, getGenreSongs(genre))
                            .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                    }
                }
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(getGenreSongs(genre))
                return true
            }
        }
        return false
    }

    private fun getGenreSongs(genre: Genre): List<Song> {
        return genreRepository.songs(genre.id)
    }
}