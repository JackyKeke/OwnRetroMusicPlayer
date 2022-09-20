package com.jackykeke.ownretromusicplayer.repository

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER
import android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
import androidx.core.database.getStringOrNull
import com.jackykeke.ownretromusicplayer.Constants
import com.jackykeke.ownretromusicplayer.extensions.getInt
import com.jackykeke.ownretromusicplayer.extensions.getLong
import com.jackykeke.ownretromusicplayer.extensions.getString
import com.jackykeke.ownretromusicplayer.extensions.getStringOrNull
import com.jackykeke.ownretromusicplayer.model.Playlist
import com.jackykeke.ownretromusicplayer.model.PlaylistSong
import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

interface PlaylistRepository {
    fun playlist(cursor: Cursor?): Playlist

    fun searchPlaylist(query: String): List<Playlist>

    fun playlist(playlistName: String): Playlist

    fun playlists(): List<Playlist>

    fun playlists(cursor: Cursor?): List<Playlist>

    fun favoritePlaylist(playlistName: String): List<Playlist>

    fun deletePlaylist(playlistId: Long)

    fun playlist(playlistId: Long): Playlist

    fun playlistSongs(playlistId: Long): List<Song>

}

class RealPlaylistRepository(private val contentProvider: ContentProvider) : PlaylistRepository {
    override fun playlist(cursor: Cursor?): Playlist = cursor.use {
        if (cursor?.moveToFirst() == true) {
            getPlaylistFromCursorImpl(cursor)
        } else {
            Playlist.empty
        }
    }

    private fun getPlaylistFromCursorImpl(cursor: Cursor): Playlist {
        val id = cursor.getLong(0)
        val name = cursor.getString(1)
        return if (name != null) {
            Playlist(id, name)
        } else {
            Playlist.empty
        }
    }


    override fun playlist(playlistName: String): Playlist = playlist(
        makePlaylistCursor(
            MediaStore.Audio.PlaylistsColumns.NAME + "=?",
            arrayOf(playlistName)
        )
    )

    private fun makePlaylistCursor(selection: String?, values: Array<String>?): Cursor? =
        //根据selection、selectionArgs  得到的是  _ID, PlaylistsColumns.NAME
        contentProvider.query(
            EXTERNAL_CONTENT_URI,
            arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.PlaylistsColumns.NAME
            ),
            selection,
            values,
            DEFAULT_SORT_ORDER
        )

    override fun playlist(playlistId: Long): Playlist = playlist(
        makePlaylistCursor(
            BaseColumns._ID + "=?",
            arrayOf(playlistId.toString())
        )
    )

    override fun searchPlaylist(query: String): List<Playlist> = playlists(
        makePlaylistCursor(
            MediaStore.Audio.PlaylistsColumns.NAME + "=?", arrayOf(query)
        )
    )

    override fun playlists(): List<Playlist> = playlists(makePlaylistCursor(null, null))

    override fun playlists(cursor: Cursor?): List<Playlist> = mutableListOf<Playlist>().apply {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                add(getPlaylistFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

    override fun favoritePlaylist(playlistName: String): List<Playlist> = playlists(
        makePlaylistCursor(
            MediaStore.Audio.PlaylistsColumns.NAME + "=?",
            arrayOf(playlistName)
        )
    )

    override fun deletePlaylist(playlistId: Long) {
        val localUri = EXTERNAL_CONTENT_URI
        val localStringBuilder = StringBuilder()
        localStringBuilder.append("_id IN (").append(playlistId).append(")")
        contentProvider.delete(localUri, localStringBuilder.toString(), null)

    }

    override fun playlistSongs(playlistId: Long): List<Song> = arrayListOf<Song>().apply {
        if (playlistId == -1L) return@apply

        val cursor = makePlaylistSongCursor(playlistId)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                add(getPlaylistFromCursorImpl(cursor, playlistId))
            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

    private fun getPlaylistFromCursorImpl(cursor: Cursor, playlistId: Long): PlaylistSong {
        val id =cursor.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
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
            composer ?: "",
            albumArtist
        )
    }

    private fun makePlaylistSongCursor(playlistId: Long): Cursor? = contentProvider.query(
        MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
        arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.TRACK,
            MediaStore.Audio.AudioColumns.YEAR,
            MediaStore.Audio.AudioColumns.DURATION,
            Constants.DATA,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
            MediaStore.Audio.AudioColumns.ARTIST, // 10
            MediaStore.Audio.Playlists.Members._ID,//11
            MediaStore.Audio.AudioColumns.COMPOSER,//12
            "album_artist"//13
        ),
        Constants.IS_MUSIC,
        null,
        MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
    )

}