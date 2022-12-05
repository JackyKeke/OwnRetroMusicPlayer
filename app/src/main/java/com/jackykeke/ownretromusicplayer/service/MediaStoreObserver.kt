package com.jackykeke.ownretromusicplayer.service

import android.database.ContentObserver
import android.os.Handler

/**
 *
 * @author keyuliang on 2022/12/1.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class MediaStoreObserver(
    private val musicService: MusicService,
    private val mHandler: Handler
) : ContentObserver(mHandler),Runnable {


    override fun onChange(selfChange: Boolean) {
        // if a change is detected, remove any scheduled callback
        // then post a new one. This is intended to prevent closely
        // spaced events from generating multiple refresh calls
//        如果检测到更改，请删除任何计划的回调，然后发布一个新回调。这是为了防止间隔很近的事件生成多个刷新调用
        mHandler.removeCallbacks(this)
        mHandler.postDelayed(this,REFRESH_DELAY)

    }

    override fun run() {
        // actually call refresh when the delayed callback fires
        // do not send a sticky broadcast here
        //在延迟回调触发时实际调用刷新不要在此处发送粘性广播
        musicService.handleAndSendChangeInternal(MusicService.MEDIA_STORE_CHANGED)
    }

    companion object {
        // milliseconds to delay before calling refresh to aggregate events
        private const val REFRESH_DELAY: Long = 500
    }

}