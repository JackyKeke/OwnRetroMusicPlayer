package com.jackykeke.ownretromusicplayer.fragments.genres

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialSharedAxis
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.adapter.song.SongAdapter
import com.jackykeke.ownretromusicplayer.databinding.FragmentPlaylistDetailBinding
import com.jackykeke.ownretromusicplayer.extensions.dipToPix
import com.jackykeke.ownretromusicplayer.fragments.base.AbsMainActivityFragment
import com.jackykeke.ownretromusicplayer.helper.menu.GenreMenuHelper
import com.jackykeke.ownretromusicplayer.model.Genre
import com.jackykeke.ownretromusicplayer.model.Song
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 *
 * @author keyuliang on 2022/12/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class GenreDetailsFragment : AbsMainActivityFragment(R.layout.fragment_playlist_detail) {

    private val arguments by navArgs<GenreDetailsFragmentArgs>()

    private val detailsViewModel: GenreDetailsViewModel by viewModel {
        parametersOf(arguments.extraGenre)
    }

    private lateinit var genre: Genre
    private lateinit var songAdapter: SongAdapter
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        _binding = FragmentPlaylistDetailBinding.bind(view)

        mainActivity.addMusicServiceEventListener(detailsViewModel)
        mainActivity.setSupportActionBar(binding.toolbar)
        binding.container.transitionName = "genre"
        genre = arguments.extraGenre
        binding.toolbar.title = arguments.extraGenre.name
        setupRecyclerView()
        detailsViewModel.getSongs().observe(viewLifecycleOwner) {
            songs(it)
        }
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())

    }

    fun songs(songs: List<Song>) {
        binding.progressIndicator.hide()
        if (songs.isNotEmpty()) songAdapter.swapDataSet(songs)
        else songAdapter.swapDataSet(emptyList())
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(requireActivity(), ArrayList(), R.layout.item_list)
        binding.recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
        songAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }

    private fun checkIsEmpty() {
        checkForPadding()
        binding.emptyEmoji.text = getEmojiByUnicode(0x1F631)
        binding.empty.isVisible = songAdapter.itemCount == 0
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun checkForPadding() {
        val height = dipToPix(52f).toInt()
        binding.recyclerView.setPadding(0, 0, 0, height)
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_genre_detail, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return GenreMenuHelper.handleMenuClick(requireActivity(), genre, item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}