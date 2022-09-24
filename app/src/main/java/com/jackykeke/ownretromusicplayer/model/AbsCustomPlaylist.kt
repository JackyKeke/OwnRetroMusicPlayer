package com.jackykeke.ownretromusicplayer.model

import code.name.monkey.retromusic.repository.LastAddedRepository
import code.name.monkey.retromusic.repository.TopPlayedRepository
import com.jackykeke.ownretromusicplayer.repository.SongRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *
 * @author keyuliang on 2022/9/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsCustomPlaylist(id:Long, name:String) :Playlist(id,name),KoinComponent {

    abstract fun songs():List<Song>

    protected val songRepository by inject<SongRepository>()

    protected val topPlayedRepository by inject<TopPlayedRepository>()

    protected val lastAddedRepository by inject<LastAddedRepository>()
}