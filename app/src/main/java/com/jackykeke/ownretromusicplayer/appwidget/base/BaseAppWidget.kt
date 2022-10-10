package com.jackykeke.ownretromusicplayer.appwidget.base

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.service.MusicService
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.APP_WIDGET_UPDATE
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.EXTRA_APP_WIDGET_NAME
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.FAVORITE_STATE_CHANGED
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.META_CHANGED
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.PLAY_STATE_CHANGED

/**
 *
 * @author keyuliang on 2022/9/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class BaseAppWidget :AppWidgetProvider() {

    /**
     * {@inheritDoc}
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        defaultAppWidget(context,appWidgetIds)
        val updateIntent = Intent(APP_WIDGET_UPDATE)
        updateIntent.putExtra(EXTRA_APP_WIDGET_NAME,NAME)
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds)
        updateIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        context.sendBroadcast(updateIntent)
    }

    abstract fun defaultAppWidget(context: Context, appWidgetIds: IntArray)

    /**
     * Handle a change notification coming over from [MusicService]
     */
    fun notifyChange(service: MusicService,what:String){
        if (hasInstances(service)){
            if (META_CHANGED ==what || PLAY_STATE_CHANGED == what || FAVORITE_STATE_CHANGED == what){
                performUpdate(service,null)
            }
        }
    }

    abstract fun performUpdate(service: MusicService, appWidgetIds: IntArray?)

    private fun hasInstances(context: Context): Boolean {

        val appWidgetManager =AppWidgetManager.getInstance(context)
        val mAppWidgetIds =appWidgetManager.getAppWidgetIds(ComponentName(context,javaClass))
        return mAppWidgetIds.isNotEmpty()
    }

    protected fun buildPendingIntent(
        context: Context,
        action:String,
        serviceName: ComponentName
    ): PendingIntent {
        val intent = Intent(action)
        intent.component = serviceName
        return if (VersionUtils.hasOreo()){
            PendingIntent.getForegroundService(context,0,intent,PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getService( context, 0, intent, if (VersionUtils.hasMarshmallow())
                PendingIntent.FLAG_IMMUTABLE
            else 0)
        }
    }

    protected fun getAlbumArtDrawable(context: Context , bitmap:Bitmap?):Drawable{
        return if (bitmap == null){
            ContextCompat.getDrawable(context, R.drawable.default_audio_art)!!
        }else{
            BitmapDrawable(context.resources,bitmap)
        }
    }

    protected fun getSongArtistAndAlbum(song: Song):String {
        val builder = StringBuilder()
        builder.append(song.artistName)
        if (song.artistName.isNotEmpty() && song.albumName.isNotEmpty()){
            builder.append(" • ")
        }
        builder.append(song.albumName)
        return builder.toString()
    }

    protected fun pushUpdate(context: Context, appWidgetIds: IntArray?, views: RemoteViews) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (appWidgetIds!=null){
            appWidgetManager.updateAppWidget(appWidgetIds,views)
        }else{
            appWidgetManager.updateAppWidget(ComponentName(context,javaClass),views)
        }

    }

    companion object{
        const val NAME: String = "app_widget"

        fun createRoundedBitmap(
            drawable: Drawable?,
            width:Int,
            height: Int,
            tl: Float,
            tr: Float,
            bl: Float,
            br: Float
        ) : Bitmap?{

            if (drawable == null) return null
            val bitmap =Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
            val c = Canvas(bitmap)
            drawable.setBounds(0,0,width,height)
            drawable.draw(c)

            val rounded = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)

            val canvas =Canvas(rounded)
            val paint = Paint()
            paint.shader = BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
            paint.isAntiAlias=true
            canvas.drawPath(
                composeRoundedRectPath( RectF(0f, 0f, width.toFloat(), height.toFloat()), tl, tr, bl, br),
            paint
                )

            return rounded
        }

        private fun composeRoundedRectPath(
            rect: RectF,
            tl: Float,
            tr: Float,
            bl: Float,
            br: Float
        ): Path {
            val path = Path()
            path.moveTo(rect.left + tl, rect.top)
            path.lineTo(rect.right - tr, rect.top)
            path.quadTo(rect.right, rect.top, rect.right, rect.top + tr)
            path.lineTo(rect.right, rect.bottom - br)
            path.quadTo(rect.right, rect.bottom, rect.right - br, rect.bottom)
            path.lineTo(rect.left + bl, rect.bottom)
            path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bl)
            path.lineTo(rect.left, rect.top + tl)
            path.quadTo(rect.left, rect.top, rect.left + tl, rect.top)
            path.close()
            return path

        }
    }
}