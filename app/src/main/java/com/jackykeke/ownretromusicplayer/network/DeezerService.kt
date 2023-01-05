package com.jackykeke.ownretromusicplayer.network

import com.jackykeke.ownretromusicplayer.model.DeezerResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */


private const val BASE_QUERY_ARTIST = "search/artist"
private const val BASE_URL = "https://api.deezer.com/"

interface DeezerService {

    @GET("$BASE_QUERY_ARTIST&limit=1")
    fun getArtistImage(
        @Query("q") artistName: String
    ): Call<DeezerResponse>

    companion object {

        operator fun invoke(client: okhttp3.Call.Factory): DeezerService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
        }
    }
}