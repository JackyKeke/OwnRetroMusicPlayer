package com.jackykeke.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.jackykeke.appthemehelper.ThemeStore

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class ATEAccentTextView @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet?=null,
    defStyleAttr:Int=0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setTextColor(ThemeStore.accentColor(context))
    }

}