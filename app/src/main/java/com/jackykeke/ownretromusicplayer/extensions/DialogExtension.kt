package com.jackykeke.ownretromusicplayer.extensions

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jackykeke.ownretromusicplayer.BuildConfig
import com.jackykeke.ownretromusicplayer.R

/**
 *
 * @author keyuliang on 2023/1/3.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun Fragment.materialDialog(title: Int): MaterialAlertDialogBuilder {
    return if (BuildConfig.DEBUG) {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme)
    } else {
        MaterialAlertDialogBuilder(requireContext())
    }.setTitle(title)
}

fun AlertDialog.colorButtons(): AlertDialog {
    setOnShowListener {
        getButton(AlertDialog.BUTTON_POSITIVE).accentTextColor()
        getButton(AlertDialog.BUTTON_NEGATIVE).accentTextColor()
        getButton(AlertDialog.BUTTON_NEUTRAL).accentTextColor()

    }
    return this
}


fun Fragment.materialDialog(): MaterialDialog =
    MaterialDialog(requireContext()).cornerRadius(res = R.dimen.m3_dialog_corner_size)

