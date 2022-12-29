package com.jackykeke.ownretromusicplayer.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.EXTRA_SONG
import com.jackykeke.ownretromusicplayer.extensions.extraNotNull
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.fragments.ReloadType
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 * @author keyuliang on 2022/12/29.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class DeleteSongsDialog : DialogFragment() {

    lateinit var libraryViewModel: LibraryViewModel

    companion object {


        fun create(song: Song): DeleteSongsDialog {
            val list = ArrayList<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): DeleteSongsDialog {
            return DeleteSongsDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to ArrayList(songs)
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        libraryViewModel = activity?.getViewModel() as LibraryViewModel

        val songs = extraNotNull<List<Song>>(EXTRA_SONG).value
        if (VersionUtils.hasR()){
            val deleteResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
                    result ->

                    if (result.resultCode == Activity.RESULT_OK){
                        if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])){
                            MusicPlayerRemote.playNextSong()
                        }
                        MusicPlayerRemote.removeFromQueue(songs)
                        reloadTabs()
                    }
                    dismiss()
                }


        }
    }

    private fun reloadTabs() {
        libraryViewModel.forceReload(ReloadType.Songs)
        libraryViewModel.forceReload(ReloadType.HomeSections)
        libraryViewModel.forceReload(ReloadType.Artists)
        libraryViewModel.forceReload(ReloadType.Albums)
    }


}