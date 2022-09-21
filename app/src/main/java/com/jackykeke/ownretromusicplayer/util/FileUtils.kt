package com.jackykeke.ownretromusicplayer.util

import android.os.Environment
import java.io.File

/**
 *
 * @author keyuliang on 2022/9/21.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object FileUtils {


}

@Suppress("Deprecation")
fun getExternalStorageDirectory(): File {
    return Environment.getExternalStorageDirectory()
}

fun getExternalStoragePublicDirectory(type:String):File{
    return Environment.getExternalStoragePublicDirectory(type)
}