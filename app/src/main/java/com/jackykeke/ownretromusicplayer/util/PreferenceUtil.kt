package com.jackykeke.ownretromusicplayer.util

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.jackykeke.ownretromusicplayer.ARTIST_DETAIL_SONG_SORT_ORDER
import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.PLAYLIST_SORT_ORDER
import com.jackykeke.ownretromusicplayer.extensions.getStringOrDefault
import com.jackykeke.ownretromusicplayer.helper.SortOrder
import com.jackykeke.ownretromusicplayer.model.Artist
import com.jackykeke.ownretromusicplayer.model.CategoryInfo

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object PreferenceUtil {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.Home, true),
        CategoryInfo(CategoryInfo.Category.Songs, true),
        CategoryInfo(CategoryInfo.Category.Albums, true),
        CategoryInfo(CategoryInfo.Category.Artists, true),
        CategoryInfo(CategoryInfo.Category.Playlists, true),
        CategoryInfo(CategoryInfo.Category.Genres, false),
        CategoryInfo(CategoryInfo.Category.Folder, false),
        CategoryInfo(CategoryInfo.Category.Search, false)
    )


    var artistDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_DETAIL_SONG_SORT_ORDER,
            SortOrder.ArtistSongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit { putString(ARTIST_DETAIL_SONG_SORT_ORDER, value) }

    var playlistSortOrder
    get() = sharedPreferences.getStringOrDefault(
        PLAYLIST_SORT_ORDER,
        SortOrder.PlaylistSortOrder.PLAYLIST_A_Z
    )
    set(value) = sharedPreferences.edit { putString(PLAYLIST_SORT_ORDER,value) }


}