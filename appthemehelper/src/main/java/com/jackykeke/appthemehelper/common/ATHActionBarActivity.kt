package com.jackykeke.appthemehelper.common

import androidx.appcompat.widget.Toolbar
import com.jackykeke.appthemehelper.util.ToolbarContentTintHelper

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class ATHActionBarActivity :ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}