package com.jackykeke.ownretromusicplayer.network

import com.jackykeke.ownretromusicplayer.network.model.LastFmAlbum
import com.jackykeke.ownretromusicplayer.network.model.LastFmArtist
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 *
 * @author keyuliang on 2022/9/21.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

interface LastFMService {

    companion object{
        private const val API_KEY = "c679c8d3efa84613dc7dcb2e8d42da4c"
        const val BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=$API_KEY"
    }

    @GET("$BASE_QUERY_PARAMETERS&method=artist.getinfo")
    suspend fun artistInfo(
        @Query("artist") artistName:String,
        @Query("lang") language: String?,
        @Header("Cache-Control") cacheControl:String?
     ):LastFmArtist

    @GET("$BASE_QUERY_PARAMETERS&method=album.getinfo")
    suspend fun albumInfo(
        @Query("artist") artistName: String,
        @Query("album") albumName: String
    ): LastFmAlbum

}
