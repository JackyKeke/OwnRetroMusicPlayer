package com.jackykeke.ownretromusicplayer.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import com.jackykeke.appthemehelper.util.ATHUtil
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteTarget
import com.jackykeke.ownretromusicplayer.glide.palette.BitmapPaletteWrapper
import com.jackykeke.ownretromusicplayer.util.ColorUtil

/**
 *
 * @author keyuliang on 2023/1/6.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class SingleColorTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(view.context, androidx.appcompat.R.attr.colorControlNormal)

    abstract fun onColorReady(color:Int)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper,
        transition: Transition<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, transition)
        onColorReady(ColorUtil.getColor(resource.palette,ATHUtil.resolveColor(view.context,androidx.appcompat.R.attr.colorPrimary)))
    }

}
