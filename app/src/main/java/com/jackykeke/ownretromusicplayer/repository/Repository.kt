package com.jackykeke.ownretromusicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import code.name.monkey.retromusic.db.PlayCountEntity
import com.jackykeke.ownretromusicplayer.db.HistoryEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.SongEntity
import com.jackykeke.ownretromusicplayer.fragments.search.Filter
import com.jackykeke.ownretromusicplayer.model.*
import com.jackykeke.ownretromusicplayer.network.model.LastFmAlbum
import com.jackykeke.ownretromusicplayer.network.model.LastFmArtist
import org.eclipse.egit.github.core.Contributor

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
interface Repository {


    fun historySong():List<HistoryEntity>
    fun favorites():LiveData<List<SongEntity>>
    fun observableHistorySongs():LiveData<List<Song>>
    fun albumById(albumId: Long): Album
    fun playlistSongs(playListId:Long):LiveData<List<SongEntity>>

    suspend fun fetchAlbums():List<Album>
    suspend fun albumByIdAsync(albumId: Long):Album
    suspend fun allSongs():List<Song>
    suspend fun fetchArtists():List<Artist>

    suspend fun albumArtists(): List<Artist>
    suspend fun fetchLegacyPlaylist(): List<Playlist>
    suspend fun fetchGenres(): List<Genre>
    suspend fun search(query: String?, filter: Filter): MutableList<Any>
    suspend fun getPlaylistSongs(playlist: Playlist): List<Song>
    suspend fun getGenre(genreId: Long): List<Song>
    suspend fun artistInfo(name: String, lang: String?, cache: String?): Result<LastFmArtist>
    suspend fun albumInfo(artist: String, album: String): Result<LastFmAlbum>
    suspend fun artistById(artistId: Long): Artist
    suspend fun albumArtistByName(name: String): Artist
    suspend fun recentArtists(): List<Artist>
    suspend fun topArtists(): List<Artist>
    suspend fun topAlbums(): List<Album>
    suspend fun recentAlbums(): List<Album>
    suspend fun recentArtistsHome(): Home
    suspend fun topArtistsHome(): Home
    suspend fun topAlbumsHome(): Home
    suspend fun recentAlbumsHome(): Home
    suspend fun favoritePlaylistHome(): Home
    suspend fun suggestions(): List<Song>
    suspend fun genresHome(): Home
    suspend fun playlists(): Home
    suspend fun homeSections(): List<Home>
    suspend fun playlist(playlistId: Long): Playlist
    suspend fun fetchPlaylistWithSongs(): List<PlaylistWithSongs>
    suspend fun playlistSongs(playlistWithSongs: PlaylistWithSongs): List<Song>
    suspend fun insertSongs(songs: List<SongEntity>)
    suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity>
    suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long
    suspend fun fetchPlaylists(): List<PlaylistEntity>
    suspend fun deleteRoomPlaylist(playlists: List<PlaylistEntity>)
    suspend fun renameRoomPlaylist(playlistId: Long, name: String)
    suspend fun deleteSongsInPlaylist(songs: List<SongEntity>)
    suspend fun removeSongFromPlaylist(songEntity: SongEntity)
    suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>)
    suspend fun favoritePlaylist(): PlaylistEntity
    suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity>
    suspend fun addSongToHistory(currentSong: Song)
    suspend fun songPresentInHistory(currentSong: Song): HistoryEntity?
    suspend fun updateHistorySong(currentSong: Song)
    suspend fun favoritePlaylistSongs(): List<SongEntity>
    suspend fun recentSongs(): List<Song>
    suspend fun topPlayedSongs(): List<Song>
    suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun deleteSongInHistory(songId: Long)
    suspend fun clearSongHistory()
    suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity>
    suspend fun playCountSongs(): List<PlayCountEntity>
    suspend fun deleteSongs(songs: List<Song>)
    suspend fun contributor(): List<Contributor>
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun searchSongs(query: String): List<Song>
    suspend fun searchAlbums(query: String): List<Album>
    suspend fun isSongFavorite(songId: Long): Boolean
    fun getSongByGenre(genreId: Long): Song
    fun checkPlaylistExists(playListId: Long): LiveData<Boolean>

}

class RealRepository(
    private val context: Context,
    private val lastFMService: LastFMService,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val lastAddedRepository: LastAddedRepository,
    private val playlistRepository: PlaylistRepository,
    private val searchRepository: RealSearchRepository,
    private val topPlayedRepository: TopPlayedRepository,
    private val roomRepository: RoomRepository,
    private val localDataRepository: LocalDataRepository,

    ):Repository{
    override fun historySong(): List<HistoryEntity> {
        TODO("Not yet implemented")
    }

    override fun favorites(): LiveData<List<SongEntity>> {
        TODO("Not yet implemented")
    }

    override fun observableHistorySongs(): LiveData<List<Song>> {
        TODO("Not yet implemented")
    }

    override fun albumById(albumId: Long): Album {
        TODO("Not yet implemented")
    }

    override fun playlistSongs(playListId: Long): LiveData<List<SongEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun playlistSongs(playlistWithSongs: PlaylistWithSongs): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAlbums(): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun albumByIdAsync(albumId: Long): Album {
        TODO("Not yet implemented")
    }

    override suspend fun allSongs(): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun albumArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLegacyPlaylist(): List<Playlist> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchGenres(): List<Genre> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String?, filter: Filter): MutableList<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaylistSongs(playlist: Playlist): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun getGenre(genreId: Long): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun artistInfo(
        name: String,
        lang: String?,
        cache: String?
    ): Result<LastFmArtist> {
        TODO("Not yet implemented")
    }

    override suspend fun albumInfo(artist: String, album: String): Result<LastFmAlbum> {
        TODO("Not yet implemented")
    }

    override suspend fun artistById(artistId: Long): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun albumArtistByName(name: String): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun recentArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun topArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun topAlbums(): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun recentAlbums(): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun recentArtistsHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun topArtistsHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun topAlbumsHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun recentAlbumsHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun favoritePlaylistHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun suggestions(): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun genresHome(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun playlists(): Home {
        TODO("Not yet implemented")
    }

    override suspend fun homeSections(): List<Home> {
        TODO("Not yet implemented")
    }

    override suspend fun playlist(playlistId: Long): Playlist {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPlaylistWithSongs(): List<PlaylistWithSongs> {
        TODO("Not yet implemented")
    }

    override suspend fun insertSongs(songs: List<SongEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity> {
        TODO("Not yet implemented")
    }

    override fun checkPlaylistExists(playListId: Long): LiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPlaylists(): List<PlaylistEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRoomPlaylist(playlists: List<PlaylistEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun renameRoomPlaylist(playlistId: Long, name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSongsInPlaylist(songs: List<SongEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeSongFromPlaylist(songEntity: SongEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun favoritePlaylist(): PlaylistEntity {
        TODO("Not yet implemented")
    }

    override suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun addSongToHistory(currentSong: Song) {
        TODO("Not yet implemented")
    }

    override suspend fun songPresentInHistory(currentSong: Song): HistoryEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun updateHistorySong(currentSong: Song) {
        TODO("Not yet implemented")
    }

    override suspend fun favoritePlaylistSongs(): List<SongEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun recentSongs(): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun topPlayedSongs(): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSongInHistory(songId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun clearSongHistory() {
        TODO("Not yet implemented")
    }

    override suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun playCountSongs(): List<PlayCountEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSongs(songs: List<Song>) =  roomRepository.deleteSongs(songs)

    override suspend fun contributor(): List<Contributor> {
        TODO("Not yet implemented")
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSongs(query: String): List<Song> {
        TODO("Not yet implemented")
    }

    override suspend fun searchAlbums(query: String): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun isSongFavorite(songId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSongByGenre(genreId: Long): Song {
        TODO("Not yet implemented")
    }
}