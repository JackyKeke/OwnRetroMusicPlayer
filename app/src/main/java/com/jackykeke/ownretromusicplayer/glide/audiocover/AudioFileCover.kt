package com.jackykeke.ownretromusicplayer.glide.audiocover

/**
 *
 * @author keyuliang on 2022/9/30.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AudioFileCover(val filePath:String) {

    override fun hashCode(): Int {
        return filePath.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is AudioFileCover){
            other.filePath == filePath
        }else false
    }

}