package com.jackykeke.ownretromusicplayer.glide.playlistPreview

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey

/**
 *
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlaylistPreviewLoader(val context: Context) :ModelLoader<PlaylistPreview,Bitmap> {
    override fun buildLoadData(
        model: PlaylistPreview,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Bitmap> {
        return ModelLoader.LoadData(ObjectKey(model),PlaylistPreviewFetcher(context,model))
    }

    override fun handles(model: PlaylistPreview): Boolean {
      return true
    }

    class Factory(val context: Context):ModelLoaderFactory<PlaylistPreview,Bitmap>{
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<PlaylistPreview, Bitmap> {
             return PlaylistPreviewLoader(context)
        }

        override fun teardown() {

        }

    }

}