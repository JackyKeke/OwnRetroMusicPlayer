package com.jackykeke.appthemehelper.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.google.android.material.materialswitch.MaterialSwitch
import com.jackykeke.appthemehelper.ATH
import com.jackykeke.appthemehelper.ThemeStore

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class ATESwitch @JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = -1
) : MaterialSwitch(context,attributeSet,defStyleAttr){

    init {
        if (!isInEditMode && !ThemeStore.isMD3Enabled(context)){
            ATH.setTint(this,ThemeStore.accentColor(context))
        }
    }

    override fun isShown(): Boolean {
        return parent!=null && isVisible
    }

}