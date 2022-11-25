package com.jackykeke.ownretromusicplayer.glide.playlistPreview

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.jackykeke.ownretromusicplayer.util.AutoGeneratedPlaylistBitmap
import kotlinx.coroutines.*
import java.util.concurrent.Executors

/**
 *
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlaylistPreviewFetcher(val context: Context, private val playlistPreview: PlaylistPreview) :
    DataFetcher<Bitmap>, CoroutineScope by GlideScope() {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        launch {
            try {
                val bitmap =
                    AutoGeneratedPlaylistBitmap.getBitmap(context, playlistPreview.songs.shuffled())
                callback.onDataReady(bitmap)
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }
    }

    override fun cleanup() {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        cancel(null)
    }

    override fun getDataClass(): Class<Bitmap> {
        return Bitmap::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

}


private val glideDispatcher: CoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + glideDispatcher)