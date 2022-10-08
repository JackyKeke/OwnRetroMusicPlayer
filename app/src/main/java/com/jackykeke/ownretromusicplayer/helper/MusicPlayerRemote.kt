package com.jackykeke.ownretromusicplayer.helper

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.SongRepository
import com.jackykeke.ownretromusicplayer.service.MusicService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 *
 * @author keyuliang on 2022/9/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object MusicPlayerRemote : KoinComponent {

    val TAG :String = MusicPlayerRemote::class.java.simpleName

    private val mConnectionMap = WeakHashMap<Context,ServiceBinder>()

    class ServiceBinder internal constructor(private val mCallback:ServiceBinder?):ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }


    }

    val musicService : MusicService ?=null

    private val songRepository by inject<SongRepository>()

    @JvmStatic
    val isPlaying: Boolean
    get() = musicService!=null&& musicService!!.isPlaying


    val nextSong: Song?
        get() = if (musicService != null) {
            musicService?.nextSong
        } else Song.emptySong

}