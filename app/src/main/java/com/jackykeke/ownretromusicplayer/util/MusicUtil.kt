package com.jackykeke.ownretromusicplayer.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Artist
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.Repository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object MusicUtil :KoinComponent {



    private val repository = get<Repository>()

    fun isVariousArtists(artistName:String?):Boolean {

        if (artistName.isNullOrEmpty()) return false

        if (artistName == Artist.VARIOUS_ARTISTS_DISPLAY_NAME) return true

        return false
    }

    fun isArtistNameUnknown(artistName:String?):Boolean {
        if (artistName.isNullOrEmpty()) return false

        if (artistName == Artist.UNKNOWN_ARTIST_DISPLAY_NAME) return true

        val tempName = artistName.trim { it <=' ' }.lowercase()
        return  tempName == "unknown" || tempName == "<unknown>"
    }


    fun getSongCountString(context: Context,songCount:Int):String{
        val songString = if(songCount == 1) context.resources
            .getString(R.string.song) else context.resources.getString(R.string.songs)
        return "$songCount $songString"
    }

    fun buildInfoString(string1: String?,string2: String?) :String{

        if (string1.isNullOrEmpty()){
            return  if (string2.isNullOrEmpty()) "" else string2
        }
        return if (string2.isNullOrEmpty()) if (string1.isNullOrEmpty()) "" else string1 else "$string1  •  $string2"

    }



    @JvmStatic
    fun getMediaStoreAlbumCoverUri(albumId: Long): Uri {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

    suspend fun isFavorite(song: Song) = repository.isSongFavorite(song.id)

    fun getSongFileUri(songId:Long) :Uri{
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        songId)
    }



}