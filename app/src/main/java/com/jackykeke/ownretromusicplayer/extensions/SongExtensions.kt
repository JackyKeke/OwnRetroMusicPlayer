package com.jackykeke.ownretromusicplayer.extensions

import android.media.MediaDescription
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import code.name.monkey.retromusic.db.PlayCountEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.db.SongEntity
import com.jackykeke.ownretromusicplayer.model.Playlist
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

val Song.uri get() = MusicUtil.getSongFileUri(songId = id)

val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(albumId)

fun ArrayList<Song>.toMediaSessionQueue():List<MediaSessionCompat.QueueItem>{
    return map{
        song ->
        val mediaDescription = MediaDescriptionCompat.Builder()
            .setMediaId(song.id.toString())
            .setTitle(song.title)
            .setSubtitle(song.artistName)
            .setIconUri(song.albumArtUri)
            .build()
        MediaSessionCompat.QueueItem(mediaDescription,song.hashCode().toLong())
    }
}

fun Song.toPlayCount(): PlayCountEntity {
    return PlayCountEntity(
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
        albumArtist = albumArtist,
        timePlayed = System.currentTimeMillis(),
        playCount = 1
    )
}

fun Song.toSongEntity(playListId: Long):SongEntity{
    return SongEntity(
        playlistCreatorId = playListId,
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

fun List<Song>.toSongsEntity(playlistEntity: PlaylistEntity): List<SongEntity> {
    return map {
        it.toSongEntity(playlistEntity.playListId)
    }
}
