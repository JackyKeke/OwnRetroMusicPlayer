package com.jackykeke.ownretromusicplayer.views

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View

/**
 *
 * @author keyuliang on 2022/12/6.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class StatusBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec), getStatusBarHeight(
                resources
            )
        )
    }

    companion object {
        fun getStatusBarHeight(r: Resources): Int {
            var result = 0
            val resourceId = r.getIdentifier("status_bar_height",
                "dimen", "android")
            if (resourceId > 0) {
                result = r.getDimensionPixelSize(resourceId)
            }
            return result
        }
    }
}