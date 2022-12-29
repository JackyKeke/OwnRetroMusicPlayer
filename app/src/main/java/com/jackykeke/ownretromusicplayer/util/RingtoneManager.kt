package com.jackykeke.ownretromusicplayer.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.showToast
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
                    cursorSong.moveToFirst()
                    Settings.System.putString(resolver,Settings.System.RINGTONE,uri.toString())
                    val message = context.getString(R.string.x_has_been_set_as_ringtone,cursorSong.getString(0))
                    context.showToast(message)
                }
            }
        }catch (ignored:SecurityException){

        }
    }

    fun requiresDialog(context: Context):Boolean{
        if (VersionUtils.hasMarshmallow()){
            // 检查指定的应用程序是否可以修改系统设置。从 API 级别 23 开始，应用程序无法修改系统设置，
            // 除非它在其清单中声明Manifest.permission.WRITE_SETTINGS权限，并且用户明确授予应用程序此功能。
            // 为了提示用户授予此批准，应用程序必须发送带有操作ACTION_MANAGE_WRITE_SETTINGS的意图，这会导致系统显示权限管理屏幕。
            //参数：
            //上下文——应用程序上下文。
            //退货：
            //如果调用应用程序可以写入系统设置，则为 true，否则为 false
            if (!Settings.System.canWrite(context)){
                return true
            }
        }
        return false
    }

    fun showDialog(context: Context){
        return MaterialAlertDialogBuilder(context,R.style.MaterialAlertDialogTheme)
            .setTitle(R.string.dialog_title_set_ringtone)
            .setMessage(R.string.dialog_message_set_ringtone)
            .setPositiveButton(android.R.string.ok,object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    intent.data = ("package:"+context.applicationContext.packageName).toUri()
                    context.startActivity(intent)
                }
            })
            .setNegativeButton(android.R.string.cancel,null)
            .create().show()
    }
}