package com.jackykeke.ownretromusicplayer.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jackykeke.ownretromusicplayer.EXTRA_SONG
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.databinding.FragmentCreatePlaylistDialogBinding
import com.jackykeke.ownretromusicplayer.extensions.colorButtons
import com.jackykeke.ownretromusicplayer.extensions.extra
import com.jackykeke.ownretromusicplayer.extensions.materialDialog
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.model.Song
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CreatePlaylistDialog : DialogFragment() {

    private var _binding: FragmentCreatePlaylistDialogBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(song: Song): CreatePlaylistDialog {
            val list = mutableListOf<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): CreatePlaylistDialog {
            return CreatePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_SONG to songs)
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCreatePlaylistDialogBinding.inflate(layoutInflater)

        val songs: List<Song> = extra<List<Song>>(EXTRA_SONG).value ?: emptyList()
        val playlistView: TextInputEditText = binding.actionNewPlaylist
        val playlistContainer: TextInputLayout = binding.actionNewPlaylistContainer
        return materialDialog(R.string.new_playlist_title)
            .setView(binding.root)
            .setPositiveButton(R.string.create_action){
                _ , _->
                val playlistName = playlistView.text.toString()
                if (!TextUtils.isEmpty(playlistName)){
                    libraryViewModel.addToPlayList(requireContext(),playlistName,songs)
                }else{
                    playlistContainer.error = "Playlist name can't be empty"
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}