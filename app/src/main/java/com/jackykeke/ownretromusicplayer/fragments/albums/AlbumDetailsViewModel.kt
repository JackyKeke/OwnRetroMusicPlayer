package com.jackykeke.ownretromusicplayer.fragments.albums

import androidx.lifecycle.*
import com.jackykeke.ownretromusicplayer.interfaces.IMusicServiceEventListener
import com.jackykeke.ownretromusicplayer.model.Album
import com.jackykeke.ownretromusicplayer.model.Artist
import com.jackykeke.ownretromusicplayer.network.model.LastFmAlbum
import com.jackykeke.ownretromusicplayer.repository.RealRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import com.jackykeke.ownretromusicplayer.network.Result

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AlbumDetailsViewModel(
    private val repository: RealRepository,
    private val albumId:Long
) :ViewModel(),IMusicServiceEventListener{

    private val albumDetails = MutableLiveData<Album>()

    init {
        fetchAlbum()
    }

    private fun fetchAlbum() {
         viewModelScope.launch(IO){
             albumDetails.postValue(repository.albumByIdAsync(albumId))
         }
    }

    fun getAlbum() :LiveData<Album> = albumDetails

    fun getArtist(artistId:Long) : LiveData<Artist> = liveData(IO){
        val artist = repository.artistById(artistId)
        emit(artist)
    }

    fun getAlbumArtist(artistName:String):LiveData<Artist> = liveData(IO){
        val artist = repository.albumArtistByName(artistName)
        emit(artist)
    }

    fun getAlbumInfo(album: Album) :LiveData<Result<LastFmAlbum>> = liveData(IO){
        emit(Result.Loading)
        emit(repository.albumInfo(album.artistName,album.title))
    }

    fun getMoreAlbums(artist:Artist):LiveData<List<Album>> = liveData(IO){
        artist.albums.filter { item ->item.id!=albumId }.let {
            albums ->
            if (albums.isNotEmpty()) emit(albums)
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
        fetchAlbum()
    }

    override fun onShuffleModeChanged() {
     }

}