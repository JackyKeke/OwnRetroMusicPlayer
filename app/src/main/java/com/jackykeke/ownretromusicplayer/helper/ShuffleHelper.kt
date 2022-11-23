package com.jackykeke.ownretromusicplayer.helper

import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2022/11/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object ShuffleHelper {


    fun makeShuffleList(listToShuffle : MutableList<Song>,current:Int){
        if (listToShuffle.isEmpty()) return
        if (current>=0){
            val song = listToShuffle.removeAt(current)
            listToShuffle.shuffle()
            listToShuffle.add(0,song)
        }else{
            listToShuffle.shuffle()
        }
    }
}