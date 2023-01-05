package com.jackykeke.ownretromusicplayer.adapter.song

import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.dialog.RemoveSongFromPlaylistDialog
import com.jackykeke.ownretromusicplayer.extensions.accentColor
import com.jackykeke.ownretromusicplayer.extensions.accentOutlineColor
import com.jackykeke.ownretromusicplayer.extensions.toSongEntity
import com.jackykeke.ownretromusicplayer.extensions.toSongsEntity
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 * @author keyuliang on 2023/1/4.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class OrderablePlaylistSongAdapter(
    private val playlist: PlaylistEntity,
    activity: FragmentActivity,
    dataSet: MutableList<Song>,
    itemLayoutRes: Int,
) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes),
    DraggableItemAdapter<OrderablePlaylistSongAdapter.ViewHolder> {


    val libraryViewModel: LibraryViewModel by activity.viewModel()

    init {
        setHasStableIds(true)
        setMultiSelectMenuRes(R.menu.menu_playlists_songs_selection)
    }


    override fun getItemId(position: Int): Long {
        return if (position!=0 ){
            dataSet[position-1].id
        }else{
            -1
        }
    }

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) OFFSET_ITEM else SONG
    }

    inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {

        init {
            dragView?.isVisible = true
        }

        val playAction :MaterialButton ? =itemView.findViewById(R.id.playAction)
        val shuffleAction :MaterialButton? = itemView.findViewById(R.id.shuffleAction)

        override var songMenuRes: Int
            get() = R.menu.menu_item_playlist_song
            set(value) {
                super.songMenuRes = value
            }

        override fun onSongMenuItemClick(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.action_remove_from_playlist ->{
                    RemoveSongFromPlaylistDialog.create(song.toSongEntity(playlist.playListId))
                        .show(activity.supportFragmentManager, "REMOVE_FROM_PLAYLIST")
                    return true
                }
            }
            return super.onSongMenuItemClick(item)

        }

    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        if (holder.itemViewType == OFFSET_ITEM){
         val viewHolder = holder as ViewHolder
            viewHolder.playAction?.let {
                it.setOnClickListener { MusicPlayerRemote.openQueue(dataSet,0,true) }
                it.accentOutlineColor()
            }

            viewHolder.shuffleAction?.let {
                it.setOnClickListener {
                    MusicPlayerRemote.openAndShuffleQueue(dataSet,true)
                }
                it.accentColor()
            }
        }else{
            super.onBindViewHolder(holder, position - 1)
        }
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<Song>) {
        when (menuItem.itemId){
            R.id.action_remove_from_playlist -> RemoveSongFromPlaylistDialog.create(selection.toSongsEntity(
                playlist
            )).show(activity.supportFragmentManager,"REMOVE_FROM_PLAYLIST")

            else -> super.onMultipleItemAction(menuItem, selection)

        }
    }

    override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
        if (dataSet.size == 0 or 1 || isInQuickSelectMode) {
            return false
        }

        val dragHandle = holder.dragView ?: return false

        val handleWidth = dragHandle.width
        val handleHeight = dragHandle.height
        val handleLeft = dragHandle.left
        val handleTop = dragHandle.top

        return (x >= handleLeft && x < handleLeft + handleWidth &&
                y >= handleTop && y < handleTop + handleHeight) && position != 0

    }


    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        dataSet.add(toPosition - 1, dataSet.removeAt(fromPosition - 1))
    }

    override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange {
        return ItemDraggableRange(1, itemCount - 1)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    fun saveSongs(playlistEntity: PlaylistEntity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            libraryViewModel.insertSongs(dataSet.toSongsEntity(playlistEntity))
        }
    }
}