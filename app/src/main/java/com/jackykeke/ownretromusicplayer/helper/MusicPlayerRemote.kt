package com.jackykeke.ownretromusicplayer.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.showToast
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

    val TAG: String = MusicPlayerRemote::class.java.simpleName

    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()


    class ServiceToken internal constructor(internal var mWrappedContext: ContextWrapper)

    var musicService: MusicService? = null

    private val songRepository by inject<SongRepository>()

    @JvmStatic
    val isPlaying: Boolean
        get() = musicService != null && musicService!!.isPlaying

    fun isPlaying(song: Song): Boolean {
        return if (!isPlaying) {
            false
        } else song.id == currentSong.id
    }

    val currentSong: Song
        get() = if (musicService != null) {
            musicService!!.currentSong
        } else Song.emptySong

    val nextSong: Song?
        get() = if (musicService != null) {
            musicService?.nextSong
        } else Song.emptySong

    var position: Int
        get() = if (musicService != null) {
            musicService!!.position
        } else -1
        set(position) {
            if (musicService != null) {
                musicService!!.position = position
            }
        }

    @JvmStatic
    val playingQueue: List<Song>
        get() = if (musicService != null) {
            musicService?.playingQueue as List<Song>
        } else listOf()

    val songProgressMillis: Int
        get() = if (musicService != null) {
            musicService!!.songProgressMillis
        } else -1

    val songDurationMillis: Int
        get() = if (musicService != null) {
            musicService!!.songDurationMillis
        } else -1

    val repeatMode: Int
        get() = if (musicService != null) {
            musicService!!.repeatMode
        } else MusicService.REPEAT_MODE_NONE

    @JvmStatic
    val shuffleMode: Int
        get() = if (musicService != null) {
            musicService!!.shuffleMode
        } else MusicService.SHUFFLE_MODE_NONE

    val audioSessionId: Int
        get() = if (musicService != null) {
            musicService!!.audioSessionId
        } else -1

    val isServiceConnected: Boolean
        get() = musicService != null

    @JvmStatic
    fun openAndShuffleQueue(queue: List<Song>, startPlaying: Boolean) {
        var startPosition = 0
        if (queue.isNotEmpty()) {
            startPosition = Random().nextInt(queue.size)
        }

        if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying)
            && musicService != null
        ) {
            openQueue(queue, startPosition, startPlaying)
            setShuffleMode(MusicService.SHUFFLE_MODE_SHUFFLE)
        }
    }

    private fun setShuffleMode(shuffleMode: Int): Boolean {
        if (musicService != null) {
            musicService!!.setShuffleMode(shuffleMode)
            return true
        }
        return false
    }

    fun seekTo(millis: Int): Int {
        return if (musicService != null) {
            musicService!!.seek(millis)
        } else -1
    }

    fun cycleRepeatMode(): Boolean {
        if (musicService != null) {
            musicService?.cycleRepeatMode()
            return true
        }
        return false
    }

    fun toggleShuffleMode(): Boolean {
        if (musicService != null) {
            musicService?.toggleShuffle()
            return true
        }
        return false
    }


    /**
     * Async
     */
    fun openQueue(queue: List<Song>, startPosition: Int, startPlaying: Boolean) {
        if (!tryToHandleOpenPlayingQueue(
                queue, startPosition, startPlaying
            ) && musicService != null
        ) {
            musicService?.openQueue(queue, startPosition, startPlaying)
        }
    }

    private fun tryToHandleOpenPlayingQueue(
        queue: List<Song>,
        startPosition: Int,
        startPlaying: Boolean
    ): Boolean {

        if (playingQueue === queue) {
            if (startPlaying) {
                playSongAt(startPosition)
            } else {
                position = startPosition
            }
            return true
        }
        return false
    }

    private fun playSongAt(position: Int) {
        musicService?.playSongAt(position)
    }


    fun getQueueDurationMillis(position: Int): Long {
        return if (musicService != null) {
            musicService!!.getQueueDurationMillis(position)
        } else -1
    }

    fun bindToService(
        context: Context,
        callback: ServiceConnection
    ): ServiceToken? {

        val realActivity = (context as Activity).parent ?: context

        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MusicService::class.java)

        try {
            contextWrapper.startActivity(intent)
        } catch (ignored: IllegalStateException) {
            kotlin.runCatching {
                ContextCompat.startForegroundService(context, intent)
            }
        }

        val binder = ServiceBinder(callback)
        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MusicService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
            )
        ) {
            mConnectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }

        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
            musicService = null
        }
    }

    fun pauseSong() {
        musicService?.pause()
    }

    /**
     * Async
     */
    fun playNextSong() {
        musicService?.playNextSong(true)
    }

    @SuppressLint("StringFormatInvalid")
    fun playNext(songs: List<Song>): Boolean {
        musicService?.let {
            if (playingQueue.isNotEmpty()) {
                it.addSongs(position + 1, songs)
            } else {
                openQueue(songs, 0, false)
            }

            val toast =
                if (songs.size == 1) musicService!!.resources.getString(R.string.added_title_to_playing_queue)
                else musicService!!.resources.getString(
                    R.string.added_x_titles_to_playing_queue,
                    songs.size
                )
            it?.showToast(toast, Toast.LENGTH_SHORT)
            return true
        }
        return false
    }

    /**
     * Async
     */
    fun playPreviousSong() {
        musicService?.playPreviousSong(true)
    }

    /**
     * Async
     */
    fun back() {
        musicService?.back(true)
    }

    fun resumePlaying() {
        musicService?.play()
    }


    class ServiceBinder internal constructor(val mCallback: ServiceConnection?) :
        ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.service
            mCallback?.onServiceConnected(className, service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mCallback?.onServiceDisconnected(name)
            musicService = null
        }

    }


    @JvmStatic
    fun removeFromQueue(songs: List<Song>): Boolean {
        if (musicService != null) {
            musicService!!.removeSong(songs)
            return true
        }
        return false
    }

    @JvmStatic
    fun removeFromQueue(song: Song): Boolean {
        if (musicService != null) {
            musicService!!.removeSong(song)
            return true
        }
        return false
    }


    fun removeFromQueue(position: Int): Boolean {
        if (musicService != null && position >= 0 && position < playingQueue.size) {
            musicService!!.removeSong(position)
            return true
        }
        return false
    }

    fun enqueue(songs: List<Song>): Boolean {
        if (musicService != null) {
            if (playingQueue.isNotEmpty()) {
                musicService?.addSongs(songs)
            } else {
                openQueue(songs, 0, false)
            }
            val toast =
                if (songs.size == 1) musicService!!.resources.getString(R.string.added_title_to_playing_queue)
                else musicService!!.resources.getString(
                    R.string.added_x_titles_to_playing_queue,
                    songs.size
                )
            musicService?.showToast(toast)
            return true
        }
        return false
    }


}