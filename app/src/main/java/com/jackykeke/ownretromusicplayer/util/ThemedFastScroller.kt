package com.jackykeke.ownretromusicplayer.util

import android.view.ViewGroup
import com.jackykeke.appthemehelper.ThemeStore.Companion.accentColor
import com.jackykeke.appthemehelper.util.ColorUtil.isColorLight
import com.jackykeke.appthemehelper.util.MaterialValueHelper.getPrimaryTextColor
import com.jackykeke.appthemehelper.util.TintHelper
import com.jackykeke.ownretromusicplayer.views.PopupBackground
import me.zhanghai.android.fastscroll.FastScroller
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupStyles
import me.zhanghai.android.fastscroll.R

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ThemedFastScroller {

    fun create(view:ViewGroup):FastScroller{
        val context = view.context
        val color = accentColor(context)
        val textColor = getPrimaryTextColor(context,isColorLight(color))
        val fastScrollerBuilder = FastScrollerBuilder(view)

        fastScrollerBuilder.useMd2Style()
        fastScrollerBuilder.setPopupStyle { popupText ->
            PopupStyles.MD2.accept(popupText)
            popupText.background = PopupBackground(context, color)
            popupText.setTextColor(textColor)
        }

        fastScrollerBuilder.setThumbDrawable(
            TintHelper.createTintedDrawable(
                context,
                R.drawable.afs_md2_thumb,
                color
            )
        )
        return fastScrollerBuilder.build()
    }
}