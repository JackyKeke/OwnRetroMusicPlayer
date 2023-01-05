package com.jackykeke.ownretromusicplayer.fragments.albums

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.adapter.song.SimpleSongAdapter
import com.jackykeke.ownretromusicplayer.databinding.FragmentAlbumDetailsBinding
import com.jackykeke.ownretromusicplayer.extensions.show
import com.jackykeke.ownretromusicplayer.extensions.surfaceColor
import com.jackykeke.ownretromusicplayer.fragments.base.AbsMainActivityFragment
import com.jackykeke.ownretromusicplayer.glide.GlideApp
import com.jackykeke.ownretromusicplayer.glide.RetroGlideExtension
import com.jackykeke.ownretromusicplayer.interfaces.IAlbumClickListener
import com.jackykeke.ownretromusicplayer.model.Album
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil
import com.jackykeke.ownretromusicplayer.util.logD
import com.jackykeke.ownretromusicplayer.util.logE
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import com.jackykeke.ownretromusicplayer.network.Result
import com.jackykeke.ownretromusicplayer.network.model.LastFmAlbum

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class AlbumDetailsFragment : AbsMainActivityFragment(R.layout.fragment_album_details),
    IAlbumClickListener {

    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private val arguments by navArgs<AlbumDetailsFragmentArgs>()
    private val detailsViewModel by viewModel<AlbumDetailsViewModel> {
        parametersOf(arguments.extraAlbumId)
    }

    private lateinit var simpleSongAdapter: SimpleSongAdapter
    private lateinit var album: Album
    private var albumArtistExists = false

    private val savedSortOrder: String
        get() = PreferenceUtil.albumDetailSongSortOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment_container
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor())
            setPathMotion(MaterialArcMotion())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAlbumDetailsBinding.bind(view)
        mainActivity.addMusicServiceEventListener(detailsViewModel)
        mainActivity.setSupportActionBar(binding.toolbar)

        binding.toolbar.title = " "
        binding.albumCoverContainer.transitionName = arguments.extraAlbumId.toString()
        postponeEnterTransition()

        detailsViewModel.getAlbum().observe(viewLifecycleOwner){
            album ->
            view.doOnPreDraw {
                startPostponedEnterTransition()
            }

            albumArtistExists = !album.albumArtist.isNullOrEmpty()
            showAlbum(album)
            binding.artistImage.transitionName = if (albumArtistExists){
                album.albumArtist
            }else{
                album.artistId.toString()
            }
        }
    }

    private fun showAlbum(album: Album) {
        if (album.songs.isEmpty()) {
            findNavController().navigateUp()
            return
        }
        this.album = album

        binding.albumTitle.text = album.title
        val songText = resources.getQuantityString(
            R.plurals.albumSongs,
            album.songCount,
            album.songCount
        )
        binding.fragmentAlbumContent.songTitle.text = songText
        if (MusicUtil.getYearString(album.year) == "-") {
            binding.albumText.text = String.format(
                "%s • %s",
                if (albumArtistExists) album.albumArtist else album.artistName,
                MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(album.songs))
            )
        } else {
            binding.albumText.text = String.format(
                "%s • %s • %s",
                album.artistName,
                MusicUtil.getYearString(album.year),
                MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(album.songs))
            )
        }
        loadAlbumCover(album)
        simpleSongAdapter.swapDataSet(album.songs)
        if (albumArtistExists) {
            detailsViewModel.getAlbumArtist(album.albumArtist.toString())
                .observe(viewLifecycleOwner) {
                    loadArtistImage(it)
                }
        } else {
            detailsViewModel.getArtist(album.artistId).observe(viewLifecycleOwner) {
                loadArtistImage(it)
            }
        }


        detailsViewModel.getAlbumInfo(album).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    logD("Loading")
                }
                is Result.Error -> {
                    logE("Error")
                }
                is Result.Success -> {
                    aboutAlbum(result.data)
                }
            }
        }
    }

    private fun aboutAlbum(lastFmAlbum: LastFmAlbum) {
        if (lastFmAlbum.album!=null){
            if (lastFmAlbum.album.wiki!=null){
                binding.fragmentAlbumContent.aboutAlbumText.show()
            }
        }
    }

    private fun loadAlbumCover(album: Album) {
        GlideApp.with(requireContext()).asBitmapPalette()
            .albumCoverOptions(album.safeGetFirstSong())
            //.checkIgnoreMediaStore()
            .load(RetroGlideExtension.getSongModel(album.safeGetFirstSong()))
            .into(object : SingleColorTarget(binding.image) {
                override fun onColorReady(color: Int) {
                    setColors(color)
                }
            })
    }


    private fun loadArtistImage(artist: Artist) {
        detailsViewModel.getMoreAlbums(artist).observe(viewLifecycleOwner) {
            moreAlbums(it)
        }
        GlideApp.with(requireContext())
            //.forceDownload(PreferenceUtil.isAllowedToDownloadMetadata())
            .load(
                RetroGlideExtension.getArtistModel(
                    artist,
                    PreferenceUtil.isAllowedToDownloadMetadata(requireContext())
                )
            )
            .artistImageOptions(artist)
            .dontAnimate()
            .dontTransform()
            .into(binding.artistImage)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

    }

    override fun onAlbumClick(albumId: Long, view: View) {

    }


}