package com.jackykeke.ownretromusicplayer.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

/**
 *
 * @author keyuliang on 2022/12/6.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class WidthFitSquareCardView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : MaterialCardView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}