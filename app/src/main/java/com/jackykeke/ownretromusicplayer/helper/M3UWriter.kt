package com.jackykeke.ownretromusicplayer.helper

import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.toSongs
import com.jackykeke.ownretromusicplayer.model.Playlist
import com.jackykeke.ownretromusicplayer.model.Song
import java.io.*
import kotlin.jvm.Throws

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object M3UWriter :M3UConstants {

    @JvmStatic
    @Throws(IOException::class)
    fun write(
        dir: File,
        playlist: Playlist
        ):File{
        dir.noExistsNowMkdirs()
        val file = File(dir,playlist.name+"."+M3UConstants.EXTENSION)
        val songs = playlist.getSongs()
        if (songs.isNotEmpty()){
            BufferedWriter(FileWriter(file)).use {
                bw->
                bw.write(M3UConstants.HEADER)
                for (song in songs) {
                    bw.newLine()
                    bw.write(M3UConstants.ENTRY + song.duration + M3UConstants.DURATION_SEPARATOR + song.artistName + " - " + song.title)
                    bw.newLine()
                    bw.write(song.data)
                }
            }
        }
        return file
    }

    fun File.noExistsNowMkdirs() = run { if (!exists()) mkdirs() }

    @JvmStatic
    @Throws(IOException::class)
    fun writeIO(dir: File, playlistWithSongs: PlaylistWithSongs): File {
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${playlistWithSongs.playlistEntity.playlistName}.${M3UConstants.EXTENSION}"
        val file = File(dir, fileName)
        val songs: List<Song> = playlistWithSongs.songs.sortedBy {
            it.songPrimaryKey
        }.toSongs()
        if (songs.isNotEmpty()) {
            BufferedWriter(FileWriter(file)).use { bw->
                bw.write(M3UConstants.HEADER)
                songs.forEach {
                    bw.newLine()
                    bw.write(M3UConstants.ENTRY + it.duration + M3UConstants.DURATION_SEPARATOR + it.artistName + " - " + it.title)
                    bw.newLine()
                    bw.write(it.data)
                }
            }
        }
        return file
    }


    fun writeIO(outputStream: OutputStream, playlistWithSongs: PlaylistWithSongs) {
        val songs = playlistWithSongs.songs.sortedBy { it.songPrimaryKey }.toSongs()
        if (songs.isNotEmpty()){
            outputStream.use {
                os ->
                os.bufferedWriter().use {
                    bw ->
                    bw.write(M3UConstants.HEADER)
                    songs.forEach {
                        bw.newLine()
                        bw.write(M3UConstants.ENTRY + it.duration + M3UConstants.DURATION_SEPARATOR + it.artistName + " - " + it.title)
                        bw.newLine()
                        bw.write(it.data)
                    }
                }
            }
        }
    }
}