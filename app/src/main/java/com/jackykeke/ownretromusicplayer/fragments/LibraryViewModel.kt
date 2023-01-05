package com.jackykeke.ownretromusicplayer.fragments

import android.content.Context
import android.os.Build.VERSION_CODES.M
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.db.HistoryEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.SongEntity
import com.jackykeke.ownretromusicplayer.extensions.showToast
import com.jackykeke.ownretromusicplayer.extensions.toSongEntity
import com.jackykeke.ownretromusicplayer.fragments.search.Filter
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.interfaces.IMusicServiceEventListener
import com.jackykeke.ownretromusicplayer.model.*
import com.jackykeke.ownretromusicplayer.repository.GenreRepository
import com.jackykeke.ownretromusicplayer.repository.RealRepository
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.util.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * @author keyuliang on 2022/12/1.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class LibraryViewModel(private val repository: RealRepository) : ViewModel(),
    IMusicServiceEventListener {

    private val _paletteColor = MutableLiveData<Int>()
    private val home = MutableLiveData<List<Home>>()
    private val suggestions = MutableLiveData<List<Song>>()
    private val albums = MutableLiveData<List<Album>>()
    private val songs = MutableLiveData<List<Song>>()
    private val artists = MutableLiveData<List<Artist>>()
    private val playlists = MutableLiveData<List<PlaylistWithSongs>>()
    private val genres = MutableLiveData<List<Genre>>()
    private val searchResults = MutableLiveData<List<Any>>()
    private val fabMargin = MutableLiveData(0)
    private val songHistory = MutableLiveData<List<Song>>()
    private val previousSongHistory = ArrayList<HistoryEntity>()
    val paletteColor: LiveData<Int> = _paletteColor


    init {
        loadLibraryContent()
    }

    private fun loadLibraryContent() = viewModelScope.launch(Dispatchers.IO) {
        fetchHomeSections()
        fetchSuggestions()
        fetchSongs()
        fetchAlbums()
        fetchArtists()
        fetchGenres()
        fetchPlaylists()
    }

    private suspend fun fetchAlbums() {
        albums.postValue(repository.fetchAlbums())
    }

    private suspend fun fetchArtists() {
        if (PreferenceUtil.albumArtistsOnly) {
            artists.postValue(repository.albumArtists())
        } else {
            artists.postValue(repository.fetchArtists())
        }
    }

    private suspend fun fetchPlaylists() {
        playlists.postValue(repository.fetchPlaylistWithSongs())
    }

    private suspend fun fetchGenres() {
        genres.postValue(repository.fetchGenres())
    }


    private suspend fun fetchSongs() {
        songs.postValue(repository.allSongs())
    }

    private suspend fun fetchSuggestions() {
        suggestions.postValue(repository.suggestions())
    }

    private suspend fun fetchHomeSections() {
        home.postValue(repository.homeSections())
    }

    fun search(query: String?, filter: Filter) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.search(query, filter)
            searchResults.postValue(result)
        }

    fun forceReload(reloadType: ReloadType) = viewModelScope.launch(Dispatchers.IO) {
        when (reloadType) {
            ReloadType.Songs -> fetchSongs()
            ReloadType.Albums -> fetchAlbums()
            ReloadType.Artists -> fetchArtists()
            ReloadType.HomeSections -> fetchHomeSections()
            ReloadType.Playlists -> fetchPlaylists()
            ReloadType.Genres -> fetchGenres()
            ReloadType.Suggestions -> fetchSuggestions()
        }
    }

    fun  updateColor(newColor:Int){
        _paletteColor.postValue(newColor)
    }

    override fun onServiceConnected() {
        logD("onServiceConnected")
    }

    override fun onServiceDisconnected() {
        logD("onServiceDisconnected")
    }

    override fun onQueueChanged() {
        logD("onQueueChanged")
    }



    override fun onPlayingMetaChanged() {
        logD("onPlayingMetaChanged")
    }

    override fun onPlayStateChanged() {
        logD("onPlayStateChanged")
    }

    override fun onRepeatModeChanged() {
        logD("onRepeatModeChanged")
    }

    override fun onShuffleModeChanged() {
        logD("onShuffleModeChanged")
    }

    override fun onFavoriteStateChanged() {
        logD("onFavoriteStateChanged")
    }

    override fun onMediaStoreChanged() {
        logD("onMediaStoreChanged")
        loadLibraryContent()
    }

    fun shuffleSongs() = viewModelScope.launch(Dispatchers.IO) {
        val songs = repository.allSongs()
        withContext(Dispatchers.Main){
            MusicPlayerRemote.openAndShuffleQueue(songs,true)
        }
    }

    fun addToPlayList(context: Context, playlistName: String, songs: List<Song>) {
        viewModelScope.launch(IO) {
            val playlists = checkPlaylistExists(playlistName)
            if (playlists.isEmpty()){
                val  playlistId:Long =
                    createPlaylist(PlaylistEntity(playlistName = playlistName))
                insertSongs(songs.map { it.toSongEntity(playlistId) })
                withContext(Main){
                    context.showToast(context.getString(
                        R.string.playlist_created_sucessfully,
                        playlistName))
                }
            }else{
                val playlist = playlists.firstOrNull()
                if (playlist!=null){
                    insertSongs(songs.map {
                        it.toSongEntity(playListId =playlist.playListId)
                    })
                }
            }
            forceReload(ReloadType.Playlists)
            withContext(Main){
                context.showToast(
                    context.getString(
                        R.string.added_song_count_to_playlist,
                        songs.size,
                        playlistName)
                )
            }
        }

    }

    suspend fun insertSongs(songs: List<SongEntity>) {
        return repository.insertSongs(songs)
    }


    private suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long {
        return repository.createPlaylist(playlistEntity)
    }


    private suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity> =
        repository.checkPlaylistExists(playlistName)

    fun deleteSongsInPlaylist(songs: List<SongEntity>){
        viewModelScope.launch(IO) {
            repository.deleteSongsInPlaylist(songs)
            forceReload(ReloadType.Playlists)
        }
    }

    fun deleteSongsFromPlaylist(playlists:List<PlaylistEntity>) = viewModelScope.launch(IO) {
        repository.deletePlaylistSongs(playlists)
    }

    fun deleteRoomPlaylist (playlists: List<PlaylistEntity>) = viewModelScope.launch(IO) {
        repository.deleteRoomPlaylist(playlists)
    }


    fun  renameRoomPlaylist (playlistId:Long,name:String)=viewModelScope.launch(IO){
        repository.renameRoomPlaylist(playlistId, name)
    }

}


enum class ReloadType {
    Songs,
    Albums,
    Artists,
    HomeSections,
    Playlists,
    Genres,
    Suggestions
}
