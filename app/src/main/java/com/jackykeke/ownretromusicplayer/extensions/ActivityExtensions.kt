package com.jackykeke.ownretromusicplayer.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup

/**
 *
 * @author keyuliang on 2022/9/27.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

inline val Activity.rootView: View get() = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)