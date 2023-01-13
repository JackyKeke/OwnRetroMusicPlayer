package com.jackykeke.ownretromusicplayer.fragments.base

import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import code.name.monkey.retromusic.interfaces.IPaletteColorHolder
import com.jackykeke.ownretromusicplayer.activities.MainActivity
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.fragments.player.PlayerAlbumCoverFragment
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 * @author keyuliang on 2023/1/13.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsPlayerFragment(@LayoutRes layout: Int) : AbsMusicServiceFragment(layout),
    Toolbar.OnMenuItemClickListener, IPaletteColorHolder, PlayerAlbumCoverFragment.Callbacks {


    val libraryViewModel: LibraryViewModel by sharedViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity


    private var playerAlbumCoverFragment:PlayerAlbumCoverFragment? = null

    override fun onMenuItemClick(item: MenuItem): Boolean {
         val song = MusicPlayerRemote.currentSong
        when(item.itemId){

        }
    }

}