package com.jackykeke.ownretromusicplayer.interfaces

/**
 *
 * @author keyuliang on 2022/9/19.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
interface IMusicServiceEventListener {

    fun onServiceConnected()

    fun onServiceDisconnected()

    fun onQueueChanged()

    fun onFavoriteStateChanged()

    fun onPlayingMetaChanged()

    fun onPlayStateChanged()

    fun onRepeatModeChanged()

    fun onShuttleModeChanged()

    fun onMediaStoreChanged()
    
}