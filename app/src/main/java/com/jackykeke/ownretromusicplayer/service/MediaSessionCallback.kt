package com.jackykeke.ownretromusicplayer.service

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import code.name.monkey.retromusic.repository.TopPlayedRepository
import com.jackykeke.ownretromusicplayer.auto.AutoMediaIDHelper
import com.jackykeke.ownretromusicplayer.model.Album
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.*
import com.jackykeke.ownretromusicplayer.util.logD
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *
 * @author keyuliang on 2022/11/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class MediaSessionCallback(private val musicService: MusicService)
    :MediaSessionCompat.Callback(),KoinComponent{

    private val songRepository by inject<SongRepository>()
    private val albumRepository by inject<AlbumRepository>()
    private val artistRepository by inject<ArtistRepository>()
    private val genreRepository by inject<GenreRepository>()
    private val playlistRepository by inject<PlaylistRepository>()
    private val topPlayedRepository by inject<TopPlayedRepository>()

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)

        val musicId = AutoMediaIDHelper.extractMusicID(mediaId!!)
        logD("Music Id $musicId")
        val itemId = musicId?.toLong() ?:-1
        val songs:ArrayList<Song> = ArrayList()
        when (val category = AutoMediaIDHelper.extractCategory(mediaId)){
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM -> {
                val album:Album = albumRepository.album(itemId)
                songs.addAll(album.songs)
                musicService.openQueue(songs,0,true)
            }
        }

    }


}