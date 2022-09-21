package com.jackykeke.ownretromusicplayer.repository

import android.provider.MediaStore
import com.jackykeke.ownretromusicplayer.helper.SortOrder
import com.jackykeke.ownretromusicplayer.model.Album
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import java.text.Collator

/**
 *
 * @author keyuliang on 2022/9/21.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

interface AlbumRepository {
    fun albums():List<Album>

    fun albums(query:String):List<Album>

    fun album(albumId: Long):Album
}

class RealAlbumRepository(private val songRepository: RealSongRepository):AlbumRepository{
    override fun albums(): List<Album> {
         val  songs =songRepository.songs(songRepository.makeSongCursor(null,null,getSongLoaderSortOrder()))
        return  splitIntoAlbums(songs)
    }

    // We don't need sorted list of songs (with sortAlbumSongs())
    // cuz we are just displaying Albums(Cover Arts) anyway and not songs
    @JvmOverloads
    fun splitIntoAlbums(songs: List<Song>, sorted:Boolean = true): List<Album> {
         val grouped = songs.groupBy { it.albumId }.map { Album(it.key,it.value) }

        if (!sorted) return grouped

        val collator = Collator.getInstance()

        return when(PreferenceUtil.albumSortOrder){
            SortOrder.AlbumSortOrder.ALBUM_A_Z -> {
                grouped.sortedWith{ a1,a2 -> collator.compare(a1.title,a2.title)}
            }
            SortOrder.AlbumSortOrder.ALBUM_Z_A -> {
                grouped.sortedWith { a1, a2 -> collator.compare(a2.title, a1.title) }
            }
            SortOrder.AlbumSortOrder.ALBUM_ARTIST -> {
                grouped.sortedWith { a1, a2 -> collator.compare(a1.albumArtist, a2.albumArtist) }
            }
            SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS -> {
                grouped.sortedByDescending { it.songCount }
            }
            else -> grouped

        }
    }

    private fun getSongLoaderSortOrder(): String {
        var  albumSortOrder = PreferenceUtil.albumSortOrder

        if (albumSortOrder == SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS)
            albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_A_Z
        return albumSortOrder+", "+PreferenceUtil.albumSongSortOrder
    }

    override fun albums(query: String): List<Album> {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(MediaStore.Audio.AudioColumns.ALBUM+" LIKE ?", arrayOf("%$query%"),getSongLoaderSortOrder())
        )
        return splitIntoAlbums(songs)
    }

    override fun album(albumId: Long): Album {
        val cursor = songRepository.makeSongCursor(
            MediaStore.Audio.AudioColumns.ALBUM_ID + "=?",
            arrayOf(albumId.toString()),
            getSongLoaderSortOrder()
        )
        val songs = songRepository.songs(cursor)
        val album = Album(albumId, songs)
        return sortAlbumSongs(album)
    }

    private fun sortAlbumSongs(album: Album): Album {
        val collator = Collator.getInstance()
        val songs = when (PreferenceUtil.albumDetailSongSortOrder) {
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST -> album.songs.sortedWith { o1, o2 ->
                o1.trackNumber.compareTo(o2.trackNumber)
            }
            SortOrder.AlbumSongSortOrder.SONG_A_Z -> {
                album.songs.sortedWith { o1, o2 -> collator.compare(o1.title, o2.title) }
            }
            SortOrder.AlbumSongSortOrder.SONG_Z_A -> {
                album.songs.sortedWith { o1, o2 -> collator.compare(o2.title, o1.title) }
            }
            SortOrder.AlbumSongSortOrder.SONG_DURATION -> album.songs.sortedWith { o1, o2 ->
                o1.duration.compareTo(o2.duration)
            }
            else -> throw IllegalArgumentException("invalid ${PreferenceUtil.albumDetailSongSortOrder}")
        }
        return album.copy(songs = songs)
    }

}