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

fun List<HistoryEntity>.fromHistoryToSongs(): List<Song> {
    return map {
        it.toSong()
    }
}

fun List<SongEntity>.toSongs(): List<Song> {
    return map {
        it.toSong()
    }
}


fun HistoryEntity.toSong(): Song {
    return Song(
        id = id,
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
        albumArtist = albumArtist
    )
}

fun SongEntity.toSong():Song{
    return Song(
        id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName, composer, albumArtist
    )
}