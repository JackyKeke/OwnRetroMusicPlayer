package com.jackykeke.ownretromusicplayer.extensions

import android.database.Cursor

/**
 *
 * @author keyuliang on 2022/9/20.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
// exception is rethrown manually in order to have a readable stacktrace
// 手动重新抛出异常以获得可读的堆栈跟踪
internal fun Cursor.getInt(columnName:String):Int{
    try {
        return getInt(getColumnIndexOrThrow(columnName))
    }catch (ex:Throwable){
        throw IllegalStateException("invalid column $columnName",ex)
    }
}


internal fun Cursor.getLong(columnName: String): Long {
    try {
        return getLong(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getString(columnName: String): String {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}
