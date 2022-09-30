package com.jackykeke.ownretromusicplayer.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 *
 * @author keyuliang on 2022/9/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@GlideModule
class RetroMusicGlideModule :AppGlideModule(){

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.prepend(PlaylistPreview::)
    }


}