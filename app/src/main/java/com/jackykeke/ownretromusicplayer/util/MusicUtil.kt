package com.jackykeke.ownretromusicplayer.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.Constants
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.getLong
import com.jackykeke.ownretromusicplayer.extensions.showToast
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote.removeFromQueue
import com.jackykeke.ownretromusicplayer.model.Artist
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.repository.Repository
import com.jackykeke.ownretromusicplayer.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.util.*

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object MusicUtil : KoinComponent {

    fun createShareSongFileIntent(context: Context, song: Song): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_STREAM, try {
                    FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName,
                        File(song.data)
                    )
                } catch (e: IllegalArgumentException) {
                    getSongFileUri(song.id)
                }
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "audio/*"
        }
    }


    private val repository = get<Repository>()

    fun isVariousArtists(artistName: String?): Boolean {

        if (artistName.isNullOrEmpty()) return false

        if (artistName == Artist.VARIOUS_ARTISTS_DISPLAY_NAME) return true

        return false
    }

    fun isArtistNameUnknown(artistName: String?): Boolean {
        if (artistName.isNullOrEmpty()) return false

        if (artistName == Artist.UNKNOWN_ARTIST_DISPLAY_NAME) return true

        val tempName = artistName.trim { it <= ' ' }.lowercase()
        return tempName == "unknown" || tempName == "<unknown>"
    }


    fun getSongCountString(context: Context, songCount: Int): String {
        val songString = if (songCount == 1) context.resources
            .getString(R.string.song) else context.resources.getString(R.string.songs)
        return "$songCount $songString"
    }

    fun buildInfoString(string1: String?, string2: String?): String {

        if (string1.isNullOrEmpty()) {
            return if (string2.isNullOrEmpty()) "" else string2
        }
        return if (string2.isNullOrEmpty()) if (string1.isNullOrEmpty()) "" else string1 else "$string1  •  $string2"

    }


    @JvmStatic
    fun getMediaStoreAlbumCoverUri(albumId: Long): Uri {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

    suspend fun isFavorite(song: Song) = repository.isSongFavorite(song.id)

    fun getSongFileUri(songId: Long): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )
    }

    fun getReadableDurationString(songDurationMills: Long): String {
        var minutes = songDurationMills / 1000 / 60
        val seconds = songDurationMills / 1000 % 60
        return if (minutes < 60) {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        } else {
            val hours = minutes / 60
            minutes %= 60
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            )
        }
    }

    fun getPlaylistInfoString(
        context: Context,
        songs: List<Song>,
    ): String {
        val duration = getTotalDuration(songs)
        return buildInfoString(
            getSongCountString(context, songs.size),
            getReadableDurationString(duration)
        )
    }

    fun getTotalDuration(songs: List<Song>): Long {
        var duration: Long = 0
        for (i in songs.indices) {
            duration += songs[i].duration
        }
        return duration
    }

    fun deleteTracks(
        activity: FragmentActivity,
        songs: List<Song>,
        safUris: List<Uri>?,
        callback: Runnable?,
    ) {
        val songRepository: SongRepository = get()
        val projection = arrayOf(BaseColumns._ID, Constants.DATA)

        // Split the query into multiple batches, and merge the resulting cursors
        // 将查询拆分为多个批次，并合并生成的游标
        var batchStart: Int
        var batchEnd = 0
        // 10^6 being the SQLite limite on the query lenth in bytes, 10 being the max number of digits in an int, used to store the track ID
        // 10^6 是 SQLite 对查询长度的限制（以字节为单位），10 是 int 中的最大位数，用于存储轨道 ID
        val batchSize = 1000000 / 10
        val songCount = songs.size

        while (batchEnd < songCount) {
            batchStart = batchEnd

            val selection = StringBuilder()
            selection.append(BaseColumns._ID + " IN (")

            var i = 0
            while (i < batchSize - 1 && batchEnd < songCount - 1) {
                selection.append(songs[batchEnd].id).append(",")
                i++
                batchEnd++
            }
            // The last element of a batch
            // The last element of a batch
            selection.append(songs[batchEnd].id).append(")")
            batchEnd++

            try {
                val cursor = activity.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection.toString(),
                    null,
                    null
                )

                if (cursor != null) {
                    // Step 1: Remove selected tracks from the current playlist, as well
                    // as from the album art cache
                    // 第 1 步：从当前播放列表以及专辑封面缓存中删除所选曲目
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        val id = cursor.getLong(BaseColumns._ID)
                        val song: Song = songRepository.song(id)
                        removeFromQueue(song)
                        cursor.moveToNext()
                    }

                    // Step 2: Remove selected tracks from the database
                    // 第 2 步：从数据库中删除选定的曲目
                    activity.contentResolver.delete(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        selection.toString(),
                        null
                    )

                    // Step 3: Remove files from card
                    // 第 3 步：从卡中删除文件
                    cursor.moveToFirst()
                    var index = batchStart
                    while (!cursor.isAfterLast){
                        val name =cursor.getString(1)
                        val safUri = if (safUris==null || safUris.size<=index) null else safUris[index]
                        SAFUtil.delete(activity,name,safUri)
                        index++
                        cursor.moveToNext()
                    }
                    cursor.close()
                }
            }catch (ignored:SecurityException){

            }

            activity.contentResolver.notifyChange("content://media".toUri(),null)
            activity.runOnUiThread {
                activity.showToast(activity.getString(R.string.deleted_x_songs, songCount))
                callback?.run()
            }


        }

    }


    suspend fun deleteTracks(context: Context, songs: List<Song>) {
        val projection = arrayOf(BaseColumns._ID, Constants.DATA)
        val selection = StringBuilder()
        selection.append(BaseColumns._ID + " IN (")
        for (i in songs.indices) {
            selection.append(songs[i].id)
            if (i < songs.size - 1) {
                selection.append(",")
            }
        }
        selection.append(")")

        var deletedCount = 0
        try {
            //查询
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection.toString(),
                null,
                null
            )
            if (cursor != null) {
                // Step 2: Remove songs from queue
                removeFromQueue(songs)

                // Step 2: Remove files from card
                cursor.moveToFirst()
//                返回光标是否指向最后一行之后的位置。
//                退货：
//                光标是否在最后一个结果之后。
                while (!cursor.isAfterLast) {
                    val id: Int = cursor.getInt(0)
                    val name: String = cursor.getString(1)

                    try {
                        // File.delete can throw a security exception
                        // File.delete 会引发安全异常
                        val f = File(name)
                        if (f.delete()) {
                            // Step 3: Remove selected track from the database
                            // 第 3 步：从数据库中删除选定的曲目
                            //删除
                            context.contentResolver.delete(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id.toLong()
                                ),
                                null,
                                null
                            )
                            deletedCount++
                        } else {
                            // I'm not sure if we'd ever get here (deletion would
                            // have to fail, but no exception thrown)
                            Log.e("MusicUtils", "Failed to delete file $name")
                        }
                        cursor.moveToFirst()
                    } catch (ex: SecurityException) {
                        cursor.moveToNext()
                    } catch (e: NullPointerException) {
                        Log.e("MusicUtils", "Failed to find file $name")
                    }
                }
                cursor.close()

            }
            withContext(Dispatchers.Main) {
                context.showToast(context.getString(R.string.deleted_x_songs, deletedCount))
            }
        } catch (ignored: SecurityException) {
        }
    }

    fun songByGenre(genreId: Long): Song {
        return repository.getSongByGenre(genreId)
    }

    fun getSectionName(mediaTitle:String?, stripPrefix:Boolean = false):String{
        var musicMediaTitle = mediaTitle
        return try {
            if (musicMediaTitle.isNullOrEmpty()){
                return "-"
            }
            musicMediaTitle = musicMediaTitle.trim { it <= ' ' }.lowercase()
            if (stripPrefix){
                if (musicMediaTitle.startsWith("the ")){
                    musicMediaTitle = musicMediaTitle.substring(4)
                } else if (musicMediaTitle.startsWith("a ")) {
                    musicMediaTitle = musicMediaTitle.substring(2)
                }
            }
            if (musicMediaTitle.isEmpty()) {
                ""
            } else musicMediaTitle.substring(0, 1).uppercase()

        }catch (e: Exception) {
            ""
        }
    }

    fun getYearString(year: Int): String {
        return if (year > 0) year.toString() else "-"
    }

    //iTunes uses for example 1002 for track 2 CD1 or 3011 for track 11 CD3.
    //this method converts those values to normal tracknumbers
    fun getFixedTrackNumber(trackNumberToFix: Int): Int {
        return trackNumberToFix % 1000
    }

}