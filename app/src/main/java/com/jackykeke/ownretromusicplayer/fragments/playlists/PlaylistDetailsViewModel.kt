package com.jackykeke.ownretromusicplayer.fragments.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.SongEntity
import com.jackykeke.ownretromusicplayer.model.Playlist
import com.jackykeke.ownretromusicplayer.repository.RealRepository

/**
 *
 * @author keyuliang on 2023/1/4.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlaylistDetailsViewModel(
    private val realRepository: RealRepository,
    private var playlist: PlaylistWithSongs
) :ViewModel() {

    fun getSongs() : LiveData<List<SongEntity>> =
        realRepository.playlistSongs(playlist.playlistEntity.playListId)

    fun playlistExists(): LiveData<Boolean> =
        realRepository.checkPlaylistExists(playlist.playlistEntity.playListId)
}