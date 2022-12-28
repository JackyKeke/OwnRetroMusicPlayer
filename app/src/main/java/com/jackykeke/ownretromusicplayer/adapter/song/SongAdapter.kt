package com.jackykeke.ownretromusicplayer.adapter.song

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import code.name.monkey.retromusic.adapter.base.AbsMultiSelectAdapter
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.adapter.base.MediaEntryViewHolder
import com.jackykeke.ownretromusicplayer.model.Song
import me.zhanghai.android.fastscroll.PopupTextProvider

/**
 *
 * @author keyuliang on 2022/12/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
open class SongAdapter(
    override val activity: FragmentActivity,
    var dataSet: MutableList<Song>,
    protected var itemLayoutRes: Int,
    showSectionName: Boolean = true
) : AbsMultiSelectAdapter<SongAdapter.ViewHolder, Song>(activity, R.menu.menu_media_selection),
    PopupTextProvider {

    open inner class ViewHolder(itemView: View): MediaEntryViewHolder(itemView) {
        protected open var songMenuRes = SongMenuHelper.MENU_RES


    }

    private var showSectionName = true

    init {
        this.showSectionName = showSectionName
        this.setHasStableIds(true)
    }



}