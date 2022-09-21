package com.jackykeke.ownretromusicplayer.helper

import android.provider.MediaStore
import com.jackykeke.ownretromusicplayer.ALBUM_ARTIST

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class SortOrder {




    interface ArtistSongSortOrder{
        companion object{

            /* Artist song sort order A-Z */
            const val SONG_A_Z=MediaStore.Audio.Media.DEFAULT_SORT_ORDER

            /* Artist song sort order Z-A */
            const val SONG_Z_A = "$SONG_A_Z DESC"

            /* Artist song sort order album */
            const val SONG_ALBUM = MediaStore.Audio.Media.ALBUM

            /* Artist song sort order year */
            const val SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC"

            /* Artist song sort order duration */
            const val SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC"

            /* Artist song sort order date */
            const val SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        }
    }


    /**
     * Playlist sort order entries.
     */
    interface PlaylistSortOrder {

        companion object {

            /* Playlist sort order A-Z */
            const val PLAYLIST_A_Z = MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER

            /* Playlist sort order Z-A */
            const val PLAYLIST_Z_A = "$PLAYLIST_A_Z DESC"

            /* Playlist sort order number of songs */
            const val PLAYLIST_SONG_COUNT = "playlist_song_count"

            /* Playlist sort order number of songs */
            const val PLAYLIST_SONG_COUNT_DESC = "$PLAYLIST_SONG_COUNT DESC"
        }
    }

    /**
     * Song sort order entries.
     */
    interface SongSortOrder{

        companion object {
            const val SONG_DEFAULT = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

            const val SONG_A_Z = MediaStore.Audio.Media.TITLE

            const val SONG_Z_A = "$SONG_A_Z DESC"

            /* Song sort order artist */
            const val SONG_ARTIST = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER

            /* Song sort order album artist */
            const val SONG_ALBUM_ARTIST = ALBUM_ARTIST

            /* Song sort order album */
            const val SONG_ALBUM = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER

            /* Song sort order year */
            const val SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC"

            /* Song sort order duration */
            const val SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC"

            /* Song sort order date */
            const val SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC"

            /* Song sort modified date */
            const val SONG_DATE_MODIFIED = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

            /* Song sort order composer*/
            const val COMPOSER = MediaStore.Audio.Media.COMPOSER

        }
    }

    /**
     * Album sort order entries.
     */
    interface  AlbumSortOrder {
        companion object {

            /* Album sort order A-Z */
            const val ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER

            /* Album sort order Z-A */
            const val ALBUM_Z_A = "$ALBUM_A_Z DESC"

            /* Album sort order songs */
            const val ALBUM_NUMBER_OF_SONGS =
                MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS + " DESC"

            /* Album Artist sort order artist */
            const val ALBUM_ARTIST = "case when lower(album_artist) is null then 1 else 0 end, lower(album_artist)"

            /* Album sort order year */
            const val ALBUM_YEAR = MediaStore.Audio.Media.YEAR + " DESC"
        }
    }


    interface  AlbumSongSortOrder{

        companion object{

            /* Album song sort order A-Z */
            const val SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

            /* Album song sort order Z-A */
            const val SONG_Z_A = "$SONG_A_Z DESC"

            /* Album song sort order track list */
            const val SONG_TRACK_LIST = (MediaStore.Audio.Media.TRACK + ", " +
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER)

            /* Album song sort order duration */
            const val SONG_DURATION = SongSortOrder.SONG_DURATION

        }
    }

    /**
     * Genre sort order entries.
     */
    interface GenreSortOrder {

        companion object {

            /* Genre sort order A-Z */
            const val GENRE_A_Z = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER

            /* Genre sort order Z-A */
            const val ALBUM_Z_A = "$GENRE_A_Z DESC"
        }
    }
}