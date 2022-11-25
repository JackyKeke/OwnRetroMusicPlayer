package com.jackykeke.ownretromusicplayer.glide.audiocover

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

/**
 *
 * @author keyuliang on 2022/11/24.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AudioFileCoverLoader :ModelLoader<AudioFileCover,InputStream>{
    override fun buildLoadData(
        audioFileCover: AudioFileCover,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>  {
        return ModelLoader.LoadData(
            ObjectKey(audioFileCover.filePath),
            AudioFileCoverFetcher(audioFileCover)
        )
    }

    override fun handles(audioFileCover: AudioFileCover): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<AudioFileCover, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AudioFileCover, InputStream> {
            return AudioFileCoverLoader()
        }

        override fun teardown() {}
    }

}