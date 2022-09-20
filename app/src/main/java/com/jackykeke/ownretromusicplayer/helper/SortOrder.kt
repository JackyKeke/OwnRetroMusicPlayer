package com.jackykeke.ownretromusicplayer.helper

import android.provider.MediaStore

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

}