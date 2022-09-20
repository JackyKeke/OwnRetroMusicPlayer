package com.jackykeke.ownretromusicplayer.db

import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun Song.toHistoryEntity(timePlayed:Long):HistoryEntity{
    return HistoryEntity(
        id=id,
        title = title,
        trackNumber = trackNumber,
        year = year,
        duration = duration,
        data = data,
        dateModified = dateModified,
        albumId = albumId,
        albumName = albumName,
        artistId = artistId,
        artistName = artistName,
        composer = composer,
        albumArtist = albumArtist,
        timePlayed = timePlayed

    )
}