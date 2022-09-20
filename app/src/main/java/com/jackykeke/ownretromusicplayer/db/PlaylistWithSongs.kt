package com.jackykeke.ownretromusicplayer.db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.jackykeke.ownretromusicplayer.model.Playlist
import kotlinx.android.parcel.Parcelize

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Parcelize
data class PlaylistWithSongs(
    @Embedded val  playlistEntity:PlaylistEntity,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "playlist_creator_id"
    )
    val songs:List<SongEntity>
) : Parcelable
