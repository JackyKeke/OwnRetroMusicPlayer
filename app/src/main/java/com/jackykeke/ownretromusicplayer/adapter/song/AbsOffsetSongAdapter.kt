package com.jackykeke.ownretromusicplayer.adapter.song

import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song

/**
 *
 * @author keyuliang on 2023/1/4.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsOffsetSongAdapter(
    activity: FragmentActivity,
    dataset: MutableList<Song>,
    @LayoutRes itemLayoutRes:Int,
) : SongAdapter(activity,dataset,itemLayoutRes){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongAdapter.ViewHolder {
        if (viewType == OFFSET_ITEM){
            val view = LayoutInflater.from(activity).inflate(R.layout.item_list_quick_actions, parent, false)
            return createViewHolder(view)
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        var positionFinal = position
        positionFinal--
        return if (positionFinal<0) -2 else super.getItemId(positionFinal)
    }

    override fun getItemCount(): Int {
        val superItemCount = super.getItemCount()
        return if (superItemCount == 0) 0 else superItemCount + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) OFFSET_ITEM else SONG
    }


    override fun getIdentifier(position: Int): Song? {
        var positionFinal = position
        positionFinal--
        return if (positionFinal < 0) null else super.getIdentifier(positionFinal)
    }


    open inner class ViewHolder(itemView: View) : SongAdapter.ViewHolder(itemView) {
        override // could also return null, just to be safe return empty song
        val song: Song
            get() = if (itemViewType == OFFSET_ITEM) Song.emptySong else dataSet[layoutPosition - 1]

        override fun onClick(v: View?) {
            if (isInQuickSelectMode && itemViewType != OFFSET_ITEM){
                toggleChecked(layoutPosition)
            }else{
                MusicPlayerRemote.openQueue(dataSet,layoutPosition -1 ,true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (itemViewType == OFFSET_ITEM) return false
            toggleChecked(layoutPosition)
            return true
        }
    }

    companion object {
        const val OFFSET_ITEM = 0
        const val SONG = 1
    }
}