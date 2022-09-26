package com.jackykeke.ownretromusicplayer.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.jackykeke.ownretromusicplayer.Constants
import com.jackykeke.ownretromusicplayer.Constants.IS_MUSIC
import com.jackykeke.ownretromusicplayer.extensions.*
import com.jackykeke.ownretromusicplayer.model.PlaylistSong
import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/9/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object PlaylistSongsLoader {


    @JvmStatic
    fun getPlaylistSongList(context: Context, playlistId: Long): List<Song> {
        val songs = mutableListOf<Song>()
        val cursor =
            makePlaylistSongCursor(
                context,
                playlistId
            )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(
                    getPlaylistSongFromCursorImpl(
                        cursor,
                        playlistId
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    // TODO duplicated in [PlaylistRepository.kt]
    private fun getPlaylistSongFromCursorImpl(cursor: Cursor, playlistId: Long): PlaylistSong {
        val id = cursor.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
        val title = cursor.getString(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val year = cursor.getInt(MediaStore.Audio.AudioColumns.YEAR)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val data = cursor.getString(Constants.DATA)
        val dateModified = cursor.getLong(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getString(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getString(MediaStore.Audio.AudioColumns.ARTIST)
        val idInPlaylist = cursor.getLong(MediaStore.Audio.Playlists.Members._ID)
        val composer = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        return PlaylistSong(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName,
            artistId,
            artistName,
            playlistId,
            idInPlaylist,
            composer,
            albumArtist
        )
    }

    private fun makePlaylistSongCursor(context: Context, playlistId: Long): Cursor? {
        try {
            return context.contentResolver.query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                arrayOf(
                    MediaStore.Audio.Playlists.Members.AUDIO_ID, // 0
                    MediaStore.Audio.AudioColumns.TITLE, // 1
                    MediaStore.Audio.AudioColumns.TRACK, // 2
                    MediaStore.Audio.AudioColumns.YEAR, // 3
                    MediaStore.Audio.AudioColumns.DURATION, // 4
                    Constants.DATA, // 5
                    MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
                    MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
                    MediaStore.Audio.AudioColumns.ALBUM, // 8
                    MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
                    MediaStore.Audio.AudioColumns.ARTIST, // 10
                    MediaStore.Audio.Playlists.Members._ID,//11
                    MediaStore.Audio.AudioColumns.COMPOSER,//12
                    "album_artist"//13
                ), IS_MUSIC, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
            )
        } catch (e: SecurityException) {
            return null
        }
    }
}