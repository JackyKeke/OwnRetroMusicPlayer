package com.jackykeke.ownretromusicplayer.extensions

import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil

/**
 *
 * @author keyuliang on 2022/10/8.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */

val Song.uri get() = MusicUtil.getSongFileUri(songId = id)

