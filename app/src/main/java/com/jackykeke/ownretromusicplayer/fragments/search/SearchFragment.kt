package com.jackykeke.ownretromusicplayer.fragments.search

import com.google.android.material.chip.ChipGroup
import com.jackykeke.ownretromusicplayer.R

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class SearchFragment  : AbsMainActivityFragment(R.layout.fragment_search),
    ChipGroup.OnCheckedStateChangeListener{
}

enum class Filter {
    SONGS,
    ARTISTS,
    ALBUMS,
    ALBUM_ARTISTS,
    GENRES,
    PLAYLISTS,
    NO_FILTER
}