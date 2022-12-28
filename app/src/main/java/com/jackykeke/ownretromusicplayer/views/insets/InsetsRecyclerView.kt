package com.jackykeke.ownretromusicplayer.views.insets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import dev.chrisbanes.insetter.applyInsetter

/**
 *
 * @author keyuliang on 2022/12/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class InsetsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode && !PreferenceUtil.isFullScreenMode)
            applyInsetter {
                type(navigationBars = true){
                    padding(vertical = true)
                }
            }
    }
}