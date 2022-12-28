package com.jackykeke.ownretromusicplayer.util

import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil.getSongFileUri

/**
 *
 * @author keyuliang on 2022/12/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object RingtoneManager {

    fun setRingtone(context:Context,song:Song){
        val uri = getSongFileUri(song.id)
        val resolver = context.contentResolver

        try {
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.TITLE),
                BaseColumns._ID + "=?",
                arrayOf(song.id.toString()),
                null
            )
            cursor.use {
                cursorSong ->
                if (cursorSong != null && cursorSong.count == 1){

                }
            }
        }catch (ignored:SecurityException){

        }
    }

}