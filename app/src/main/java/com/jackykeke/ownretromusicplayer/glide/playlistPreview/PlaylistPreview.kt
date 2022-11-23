package com.jackykeke.ownretromusicplayer.glide.playlistPreview

import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.toSongs
import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/11/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class PlaylistPreview(val playlistWithSongs: PlaylistWithSongs) {

    val playlistEntity: PlaylistEntity get() = playlistWithSongs.playlistEntity
    val songs: List<Song> get() = playlistWithSongs.songs.toSongs()


    override fun equals(other: Any?): Boolean {
       if (other is PlaylistPreview){

           if (other.playlistEntity.playListId != playlistEntity.playListId){
               return false
           }
           if (other.songs.size != songs.size) {
               return false
           }
           return true
       }
        return false
    }

    override fun hashCode(): Int {
        var result = playlistEntity.playListId.hashCode()
        result = 31 * result + playlistWithSongs.songs.size
        return result
    }

}