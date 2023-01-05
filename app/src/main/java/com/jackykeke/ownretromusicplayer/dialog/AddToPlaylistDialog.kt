package com.jackykeke.ownretromusicplayer.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.jackykeke.ownretromusicplayer.EXTRA_PLAYLIST
import com.jackykeke.ownretromusicplayer.EXTRA_PLAYLISTS
import com.jackykeke.ownretromusicplayer.EXTRA_SONG
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.db.PlaylistEntity
import com.jackykeke.ownretromusicplayer.extensions.colorButtons
import com.jackykeke.ownretromusicplayer.extensions.extraNotNull
import com.jackykeke.ownretromusicplayer.extensions.materialDialog
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.model.Song
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 * @author keyuliang on 2023/1/4.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AddToPlaylistDialog :DialogFragment() {

    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object{
        fun create(playlistEntities :List<PlaylistEntity>,song: Song):AddToPlaylistDialog{
            val list:MutableList<Song> = mutableListOf()
            list.add(song)
            return create(playlistEntities, list)
        }

        fun create(
            playlistEntities: List<PlaylistEntity>,
            songs: List<Song>
        ): AddToPlaylistDialog {
            return AddToPlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to songs,
                    EXTRA_PLAYLISTS to playlistEntities
                )
            }
        }


    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlistEntities = extraNotNull<List<PlaylistEntity>>(EXTRA_PLAYLISTS).value
        val songs = extraNotNull<List<Song>>(EXTRA_SONG).value

        val playlistNames = mutableListOf<String>()
        playlistNames.add(requireContext().resources.getString(R.string.action_new_playlist))
        for (entity: PlaylistEntity in playlistEntities) {
            playlistNames.add(entity.playlistName)
        }

        return materialDialog(R.string.add_playlist_title)
            .setItems(playlistNames.toTypedArray()){
                dialog,which ->
                if (which==0){
                    showCreateDialog(songs)
                }else{
                    libraryViewModel.addToPlayList(requireContext(), playlistNames[which], songs)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons()
    }

    private fun showCreateDialog(songs: List<Song>) {
        CreatePlaylistDialog.create(songs).show(requireActivity().supportFragmentManager, "Dialog")
    }

}