package com.jackykeke.ownretromusicplayer.util

import android.content.Context
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Artist
import org.koin.core.component.KoinComponent

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object MusicUtil :KoinComponent {




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


}