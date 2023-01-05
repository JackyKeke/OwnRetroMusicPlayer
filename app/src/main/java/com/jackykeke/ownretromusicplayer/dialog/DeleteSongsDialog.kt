package com.jackykeke.ownretromusicplayer.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.DialogFragment
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.EXTRA_SONG
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.activities.saf.SAFGuideActivity
import com.jackykeke.ownretromusicplayer.extensions.extraNotNull
import com.jackykeke.ownretromusicplayer.extensions.materialDialog
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import com.jackykeke.ownretromusicplayer.fragments.ReloadType
import com.jackykeke.ownretromusicplayer.helper.MusicPlayerRemote
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.SAFUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        if (VersionUtils.hasR()) {
            val deleteResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->

                    if (result.resultCode == Activity.RESULT_OK) {
                        if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
                            MusicPlayerRemote.playNextSong()
                        }
                        MusicPlayerRemote.removeFromQueue(songs)
                        reloadTabs()
                    }
                    dismiss()
                }

            val pendingIntent =
                MediaStore.createDeleteRequest(requireContext().contentResolver, songs.map {
                    MusicUtil.getSongFileUri(it.id)
                })
            deleteResultLauncher.launch(
                IntentSenderRequest.Builder(pendingIntent.intentSender).build()
            )
            return super.onCreateDialog(savedInstanceState)

        } else {
            val pair = if (songs.size > 1) {
                Pair(
                    R.string.delete_song_title,
                    String.format(getString(R.string.delete_x_songs), songs.size).parseAsHtml()
                )
            } else {
                Pair(
                    R.string.delete_song_title,
                    String.format(getString(R.string.delete_song_x), songs[0].title).parseAsHtml()
                )
            }

            return materialDialog()
                .title(pair.first)
                .message(text = pair.second)
                .noAutoDismiss()
                .negativeButton(android.R.string.cancel) {
                    dismiss()
                }
                .positiveButton(R.string.action_delete)
                {
                    if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
                        MusicPlayerRemote.playNextSong()
                    }
                    if (!SAFUtil.isSAFRequiredForSongs(songs)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            dismiss()
                            MusicUtil.deleteTracks(requireContext(), songs)
                            reloadTabs()
                        }
                    } else {
                        if (SAFUtil.isSDCardAccessGranted(requireActivity())) {
                            deleteSongs(songs)
                        }else{
                            startActivityForResult(
                                Intent(requireActivity(), SAFGuideActivity::class.java),
                                SAFGuideActivity.REQUEST_CODE_SAF_GUIDE
                            )
                        }
                    }

                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SAFGuideActivity.REQUEST_CODE_SAF_GUIDE ->{
                SAFUtil.openTreePicker(this)
            }

            SAFUtil.REQUEST_SAF_PICK_TREE,
            SAFUtil.REQUEST_SAF_PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    SAFUtil.saveTreeUri(requireActivity(), data)
                    val songs = extraNotNull<List<Song>>(EXTRA_SONG).value
                    deleteSongs(songs)
                }
            }
        }
    }

    fun deleteSongs(songs: List<Song>) {
        CoroutineScope(Dispatchers.IO).launch {
            dismiss()
            MusicUtil.deleteTracks(requireActivity(), songs, null, null)
            reloadTabs()
        }
    }

    private fun reloadTabs() {
        libraryViewModel.forceReload(ReloadType.Songs)
        libraryViewModel.forceReload(ReloadType.HomeSections)
        libraryViewModel.forceReload(ReloadType.Artists)
        libraryViewModel.forceReload(ReloadType.Albums)
    }


}