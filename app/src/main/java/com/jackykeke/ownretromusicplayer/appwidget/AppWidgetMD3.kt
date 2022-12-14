package com.jackykeke.ownretromusicplayer.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.jackykeke.appthemehelper.util.MaterialValueHelper
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.MainActivity
import com.jackykeke.ownretromusicplayer.appwidget.base.BaseAppWidget
import com.jackykeke.ownretromusicplayer.extensions.getTintedDrawable
import com.jackykeke.ownretromusicplayer.glide.GlideApp
import com.jackykeke.ownretromusicplayer.glide.RetroGlideExtension
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteWrapper
import com.jackykeke.ownretromusicplayer.service.MusicService
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_REWIND
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_SKIP
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_TOGGLE_PAUSE
import com.jackykeke.ownretromusicplayer.util.DensityUtil
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/9/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AppWidgetMD3 : BaseAppWidget() {


    private var target: Target<BitmapPaletteWrapper>? = null // for cancellation


    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     * 初始化给定的小部件为默认状态，在那里我们启动音乐默认点击和隐藏

    服务未运行时的操作。
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.app_widget_md3)
        appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_audio_art)
        val secondaryColor = MaterialValueHelper.getSecondaryTextColor(context, true)
        appWidgetView.setImageViewBitmap(
            R.id.button_next,
            context.getTintedDrawable(R.drawable.ic_skip_next, secondaryColor).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev,
            context.getTintedDrawable(R.drawable.ic_skip_previous,secondaryColor).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause,
            context.getTintedDrawable(
                R.drawable.ic_play_arrow_white_32dp,
                secondaryColor
            ).toBitmap()
        )

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)


    }



    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action =Intent(context,MainActivity::class.java)
            .putExtra(MainActivity.EXPAND_PANEL,PreferenceUtil.isExpandPanel)

        val serviceName=ComponentName(context,MusicService::class.java)

        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        var pendingIntent = PendingIntent.getActivity(context,0,action,if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0)
        views.setOnClickPendingIntent(R.id.image ,pendingIntent)
        views.setOnClickPendingIntent(R.id.media_titles, pendingIntent)

        // Previous track
        pendingIntent = buildPendingIntent(context, ACTION_REWIND, serviceName)
        views.setOnClickPendingIntent(R.id.button_prev, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(context, ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)

        // Next track
        pendingIntent = buildPendingIntent(context, ACTION_SKIP, serviceName)
        views.setOnClickPendingIntent(R.id.button_next, pendingIntent)
    }

    /**
     * Update all active widget instances by pushing changes
     */
    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName,R.layout.app_widget_md3)

        val isPlaying =service.isPlaying
        val song = service.currentSong

        //Set the titles and artwork
        if (song.title.isEmpty() && song.artistName.isEmpty()){
            appWidgetView.setViewVisibility(R.id.media_titles,View.INVISIBLE)
        }else{
            appWidgetView.setViewVisibility(R.id.media_titles, View.VISIBLE)
            appWidgetView.setTextViewText(R.id.title, song.title)
            appWidgetView.setTextViewText(R.id.text, getSongArtistAndAlbum(song))
        }

        // Set correct drawable for pause state
        val playPauseRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow_white_32dp
        appWidgetView.setImageViewBitmap(R.id.button_toggle_play_pause,
            service.getTintedDrawable(
                playPauseRes,
                MaterialValueHelper.getSecondaryTextColor(service,true)
            ).toBitmap()
        )


        // Set prev/next button drawables
        appWidgetView.setImageViewBitmap(
            R.id.button_next,
            service.getTintedDrawable(
                R.drawable.ic_skip_next,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev,
            service.getTintedDrawable(
                R.drawable.ic_skip_previous,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )

        linkButtons(service,appWidgetView)

        if (imageSize == 0) {
            imageSize =
                service.resources.getDimensionPixelSize(R.dimen.app_widget_card_image_size)
        }

        if (cardRadius == 0f){
            cardRadius = DensityUtil.dip2px(service,8f).toFloat()
        }


        // Load the album cover async and push the update on completion
        //加载专辑封面异步和推更新完成
        service.runOnUiThread{
            if (target !=null){
                Glide.with(service).clear(target)
            }
            target = GlideApp.with(service).asBitmapPalette().songCoverOptions(song)
                .load(RetroGlideExtension.getSongModel(song))
                .centerCrop()
                .into(object : CustomTarget<BitmapPaletteWrapper>(imageSize, imageSize) {
                    override fun onResourceReady(
                        resource: BitmapPaletteWrapper,
                        transition: Transition<in BitmapPaletteWrapper>?,
                    ) {
                        val palette = resource.palette
                        update(
                            resource.bitmap, palette.getVibrantColor(
                                palette.getMutedColor(
                                    MaterialValueHelper.getSecondaryTextColor(
                                        service, true
                                    )
                                )
                            )
                        )
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        update(null, MaterialValueHelper.getSecondaryTextColor(service, true))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                    private fun update(bitmap: Bitmap?, color: Int) {
                        // Set correct drawable for pause state
                        appWidgetView.setImageViewBitmap(
                            R.id.button_toggle_play_pause,
                            service.getTintedDrawable(playPauseRes, color).toBitmap()
                        )

                        // Set prev/next button drawables
                        appWidgetView.setImageViewBitmap(
                            R.id.button_next,
                            service.getTintedDrawable(R.drawable.ic_skip_next, color).toBitmap()
                        )
                        appWidgetView.setImageViewBitmap(
                            R.id.button_prev,
                            service.getTintedDrawable(R.drawable.ic_skip_previous, color).toBitmap()
                        )

                        val image = getAlbumArtDrawable(service, bitmap)
                        val roundedBitmap = createRoundedBitmap(
                            image,
                            imageSize,
                            imageSize,
                            cardRadius,
                            cardRadius,
                            cardRadius,
                            cardRadius
                        )
                        appWidgetView.setImageViewBitmap(R.id.image, roundedBitmap)

                        pushUpdate(service, appWidgetIds, appWidgetView)
                    }
                })
        }

    }


    companion object{
        const val NAME = "app_widget_md3"

        private var mInstance: AppWidgetMD3? = null
        private var imageSize = 0
        private var cardRadius = 0F

        val instance: AppWidgetMD3
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetMD3()
                }
                return mInstance!!
            }
    }

}