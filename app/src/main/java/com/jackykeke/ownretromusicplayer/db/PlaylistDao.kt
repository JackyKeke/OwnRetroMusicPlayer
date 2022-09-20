package com.jackykeke.ownretromusicplayer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jackykeke.ownretromusicplayer.model.Playlist

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Dao
interface PlaylistDao {

    @Insert
    suspend fun createPlaylist(playlistEntity: PlaylistEntity):Long


    @Query("UPDATE PlaylistEntity SET playlist_name= :name WHERE playlist_id= :playlistId")
    suspend fun renamePlaylist(playlistId: Long,name:String)

    @Query("SELECT * FROM PlaylistEntity WHERE playlist_name= :name")
    fun playlist(name:String):List<PlaylistEntity>

    @Query("SELECT * FROM PlaylistEntity")
    suspend fun playlists():List<PlaylistEntity>

    @Query("DELETE FROM SongEntity WHERE playlist_creator_id= :playlistId ")
    suspend fun deletePlaylistSongs(playlistId: Long)

    @Query("DELETE FROM SongEntity WHERE playlist_creator_id = :playlistId AND id = :songId")
    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long)

    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    suspend fun playlistsWithSongs(): List<PlaylistWithSongs>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongsToPlaylist(songEntities: List<SongEntity>)

    @Query("SELECT * FROM SongEntity WHERE playlist_creator_id = :playlistId AND id = :songId")
    suspend fun isSongExistsInPlaylist(playlistId: Long, songId: Long): List<SongEntity>

    @Query("SELECT * FROM SongEntity WHERE playlist_creator_id = :playlistId ORDER BY song_key asc")
    fun songsFromPlaylist(playlistId: Long): LiveData<List<SongEntity>>

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylists(playlistEntities: List<PlaylistEntity>)

    @Delete
    suspend fun deletePlaylistSongs(songs: List<SongEntity>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM SongEntity ,(SELECT playlist_id FROM PlaylistEntity WHERE playlist_name= :playlistName LIMIT 1) AS playlist WHERE playlist_creator_id= playlist.playlist_id")
    fun favoritesSongsLiveData(playlistName: String): LiveData<List<SongEntity>>

    @Query("SELECT * FROM SongEntity WHERE playlist_creator_id= :playlistId")
    fun favoritesSongs(playlistId: Long): List<SongEntity>

    @Query("SELECT EXISTS(SELECT * FROM PlaylistEntity WHERE playlist_id = :playlistId)")
    fun checkPlaylistExists(playlistId: Long): LiveData<Boolean>
}