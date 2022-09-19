package com.jackykeke.ownretromusicplayer.model

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
data class Album(
    val id:Long,
    val songs:List<Song>
){

    val title:String
    get() = safeGetFirstSong().albumName

    val artistId:Long
    get() = safeGetFirstSong().artistId

    val artistName : String
        get() = safeGetFirstSong().artistName

    val year:Int
    get() = safeGetFirstSong().year

    val dateModified:Long
    get() = safeGetFirstSong().dateModified

    val songCount :Int
    get() = songs.size

    val albumArtist: String?
        get() = safeGetFirstSong().albumArtist


    fun safeGetFirstSong():Song{
        return songs.firstOrNull()?:Song.emptySong
    }

    companion object{
        val empty = Album(-1, emptyList())
    }

}
