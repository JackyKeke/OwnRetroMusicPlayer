package com.jackykeke.ownretromusicplayer.glide

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.glide.audiocover.AudioFileCover
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteWrapper
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil.getMediaStoreAlbumCoverUri
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil

/**
 *
 * @author keyuliang on 2022/9/30.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@GlideExtension
object RetroGlideExtension {

    private const val DEFAULT_ARTIST_IMAGE =
        R.drawable.default_artist_art
    private const val DEFAULT_SONG_IMAGE: Int = R.drawable.default_audio_art
    private const val DEFAULT_ALBUM_IMAGE = R.drawable.default_album_art
    private const val DEFAULT_ERROR_IMAGE_BANNER = R.drawable.material_design_default

    private val DEFAULT_DISK_CACHE_STRATEGY_ARTIST = DiskCacheStrategy.RESOURCE
    private val DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.NONE

    private const val DEFAULT_ANIMATION = android.R.anim.fade_in


    @JvmStatic
    @GlideType(BitmapPaletteWrapper::class)
    fun asBitmapPalette(requestBuilder: RequestBuilder<BitmapPaletteWrapper>): RequestBuilder<BitmapPaletteWrapper> {
        return requestBuilder
    }

    private fun getSongModel(song: Song,ignoreMediaStore:Boolean):Any{
        return if (ignoreMediaStore){
            AudioFileCover(song.data)
        }else{
            getMediaStoreAlbumCoverUri(song.albumId)
        }
    }

    fun getSongModel(song: Song):Any{
        return getSongModel(song,PreferenceUtil.isIgnoreMediaStoreArtwork)
    }

}