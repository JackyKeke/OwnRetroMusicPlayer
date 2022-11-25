package com.jackykeke.ownretromusicplayer.service.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.MainActivity
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.service.MusicService
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_QUIT
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_REWIND
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_SKIP
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_TOGGLE_PAUSE
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.TOGGLE_FAVORITE
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import code.name.monkey.retromusic.glide.GlideApp
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jackykeke.ownretromusicplayer.glide.RetroGlideExtension

/**
 *
 * @author keyuliang on 2022/11/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlayingNotificationImpl24(
    val context: MusicService,
    mediaSessionToken: MediaSessionCompat.Token
) : PlayingNotification(context) {

    init{
        val action = Intent(context,MainActivity::class.java)
        action.putExtra(MainActivity.EXPAND_PANEL,PreferenceUtil.isExpandPanel)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val clickIntent = PendingIntent.getActivity(
            context,
            0,
            action,
            PendingIntent.FLAG_UPDATE_CURRENT or if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (VersionUtils.hasMarshmallow())
                PendingIntent.FLAG_IMMUTABLE
            else 0)
        )
        val toggleFavorite = buildFavoriteAction(false)
        val playPauseAction = buildPlayAction(true)
        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous,
            context.getString(R.string.action_previous),
            retrievePlaybackAction(ACTION_REWIND)
        )
        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            context.getString(R.string.action_next),
            retrievePlaybackAction(ACTION_SKIP)
        )
        val dismissAction = NotificationCompat.Action(
            R.drawable.ic_close,
            context.getString(R.string.action_cancel),
            retrievePlaybackAction(ACTION_QUIT)
        )
        setSmallIcon(R.drawable.ic_notification)
        setContentIntent(clickIntent)
        setDeleteIntent(deleteIntent)
        setShowWhen(false)
        addAction(toggleFavorite)
        addAction(previousAction)
        addAction(playPauseAction)
        addAction(nextAction)
        if (VersionUtils.hasS()) {
            addAction(dismissAction)
        }

        setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionToken)
                .setShowActionsInCompactView(1, 2, 3)
        )
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    }

    private fun buildPlayAction(isPlaying: Boolean): NotificationCompat.Action {
        val playButtonResId = if (isPlaying) R.drawable.ic_favorite else R.drawable.ic_favorite_border

        return NotificationCompat.Action.Builder(
            playButtonResId,
            context.getString(R.string.action_play_pause),
            retrievePlaybackAction(ACTION_TOGGLE_PAUSE)
        ).build()

    }

    private fun buildFavoriteAction(isFavorite: Boolean): NotificationCompat.Action {
        val favoriteResId= if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border

        return NotificationCompat.Action.Builder(
            favoriteResId,
            context.getString(R.string.action_toggle_favorite),
            retrievePlaybackAction(TOGGLE_FAVORITE)).build()
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {

        val serviceName  = ComponentName(context ,MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or     if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0
        )

    }


    override fun updateMetadata(song: Song, onUpdate: () -> Unit) {

        if(song== Song.emptySong) return
        setContentTitle(song.title)
        setContentText(song.artistName)
        setSubText(song.albumName)

        val bigNotificationImageSize =  context.resources
            .getDimensionPixelSize(R.dimen.notification_big_image_size)

        GlideApp.with(context)
            .asBitmap()
            .songCoverOptions(song)
            .load(RetroGlideExtension.getSongModel(song))
            //.checkIgnoreMediaStore()
            .centerCrop()
            .into(object : CustomTarget<Bitmap>(
                bigNotificationImageSize,
                bigNotificationImageSize
            ) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setLargeIcon(resource)
                    onUpdate()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.default_audio_art
                        )
                    )
                    onUpdate()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.default_audio_art
                        )
                    )
                    onUpdate()
                }
            })

    }

    @SuppressLint("RestrictedApi")
    override fun setPlaying(isPlaying: Boolean) {
        mActions[2]=buildPlayAction(isPlaying)
    }

    @SuppressLint("RestrictedApi")
    override fun updateFavorite(isFavorite: Boolean) {
        mActions[0] = buildFavoriteAction(isFavorite)
    }




    companion object {

        fun from(
            context: MusicService,
            notificationManager: NotificationManager,
            mediaSession: MediaSessionCompat,
        ): PlayingNotification {
            if (VersionUtils.hasOreo()) {
                createNotificationChannel(context, notificationManager)
            }
            return PlayingNotificationImpl24(context, mediaSession.sessionToken)
        }
    }
}