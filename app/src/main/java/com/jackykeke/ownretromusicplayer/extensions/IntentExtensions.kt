package com.jackykeke.ownretromusicplayer.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.OutputStream

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

fun Fragment.createNewFile(
    mimeType: String,
    fileName: String,
    write: (outputStream: OutputStream?, data: Uri?) -> Unit
) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = mimeType
        putExtra(Intent.EXTRA_TITLE,fileName)
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == Activity.RESULT_OK){
                write(
                    context?.contentResolver?.openOutputStream(result?.data?.data!!),
                    result.data?.data
                )
            }
        }
    startForResult.launch(intent)
}





