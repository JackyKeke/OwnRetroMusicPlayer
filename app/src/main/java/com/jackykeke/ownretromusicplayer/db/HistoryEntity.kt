package com.jackykeke.ownretromusicplayer.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Entity
class HistoryEntity(
    @PrimaryKey
    val id: Long,
    val title: String,

    @ColumnInfo(name = "track_number")
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val data: String,

    @ColumnInfo(name = "date_modified")
    val dateModified: Long,
    @ColumnInfo(name = "album_id")
    val albumId: Long,
    @ColumnInfo(name = "album_name")
    val albumName: String,
    @ColumnInfo(name = "artist_id")
    val artistId: Long,
    @ColumnInfo(name = "artist_name")
    val artistName: String,
    val composer: String?,
    @ColumnInfo(name = "album_artist")
    val albumArtist: String?,
    @ColumnInfo(name = "time_played")
    val timePlayed: Long

)