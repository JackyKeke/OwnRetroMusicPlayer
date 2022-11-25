package com.jackykeke.ownretromusicplayer.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.jackykeke.ownretromusicplayer.glide.audiocover.AudioFileCover
import com.jackykeke.ownretromusicplayer.glide.audiocover.AudioFileCoverLoader
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteTranscoder
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteWrapper
import com.jackykeke.ownretromusicplayer.glide.playlistPreview.PlaylistPreview
import com.jackykeke.ownretromusicplayer.glide.playlistPreview.PlaylistPreviewLoader
import java.io.InputStream

/**
 *
 * @author keyuliang on 2022/9/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@GlideModule
class RetroMusicGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.prepend(
            PlaylistPreview::class.java,
            Bitmap::class.java,
            PlaylistPreviewLoader.Factory(context)
        )
        registry.prepend(
            AudioFileCover::class.java,
            InputStream::class.java,
            AudioFileCoverLoader.Factory()
        )
        registry.register(
            Bitmap::class.java, BitmapPaletteWrapper::class.java,
            BitmapPaletteTranscoder()
        )
    }


    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}