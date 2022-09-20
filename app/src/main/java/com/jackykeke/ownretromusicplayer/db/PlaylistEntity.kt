package com.jackykeke.ownretromusicplayer.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jackykeke.ownretromusicplayer.model.Playlist
import kotlinx.android.parcel.Parcelize

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Entity
@Parcelize
class PlaylistEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playListId:Long =0 ,

    @ColumnInfo(name = "playlist_name")
    val playlistName:String

) : Parcelable