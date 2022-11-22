package com.jackykeke.ownretromusicplayer.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/11/22.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class PlayingNotification(context: Context):
    NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID){

    abstract fun updateMetadata(song: Song, onUpdate:()->Unit)

    abstract fun setPlaying(isPlaying: Boolean)

    abstract fun updateFavorite(isFavorite: Boolean)

    companion object{
        const val NOTIFICATION_CONTROLS_SIZE_MULTIPLIER = 1.0f
        internal const val NOTIFICATION_CHANNEL_ID = "playing_notification"
        const val NOTIFICATION_ID = 1

        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(
            context: Context,
            notificationManager: NotificationManager){
            var notificationChannel:NotificationChannel?=notificationManager.getNotificationChannel(
                NOTIFICATION_CHANNEL_ID)
            if (notificationChannel == null){
                notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.playing_notification_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.description= context.getString(R.string.playing_notification_description)
                notificationChannel.enableLights(false)
                notificationChannel.enableVibration(false)
                notificationChannel.setShowBadge(false)

                notificationManager.createNotificationChannel(notificationChannel)
            }

        }

    }






}