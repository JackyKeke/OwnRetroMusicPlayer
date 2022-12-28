package com.jackykeke.ownretromusicplayer.fragments.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackykeke.ownretromusicplayer.interfaces.IMusicServiceEventListener
import com.jackykeke.ownretromusicplayer.model.Genre
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.RealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * @author keyuliang on 2022/12/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class GenreDetailsViewModel(
    private val realRepository: RealRepository,
    private val genre: Genre
) :ViewModel() ,IMusicServiceEventListener{

    private val _playListSongs = MutableLiveData<List<Song>>()
    private val _genre = MutableLiveData<Genre>().apply {
        postValue(genre)
    }

    fun getSongs():LiveData<List<Song>> = _playListSongs

    fun getGenre():LiveData<Genre> = _genre

    init {
        loadGenreSongs(genre)
    }

    private fun loadGenreSongs(genre: Genre)  = viewModelScope.launch(Dispatchers.IO) {
        val songs = realRepository.getGenre(genre.id)
        withContext(Dispatchers.Main){
            _playListSongs.postValue(songs)
        }
    }


    override fun onServiceConnected() {

    }

    override fun onServiceDisconnected() {

    }

    override fun onQueueChanged() {

    }

    override fun onFavoriteStateChanged() {

    }

    override fun onPlayingMetaChanged() {

    }

    override fun onPlayStateChanged() {

    }

    override fun onRepeatModeChanged() {

    }

    override fun onMediaStoreChanged() {
        loadGenreSongs(genre)
    }

    override fun onShuffleModeChanged() {

    }

}