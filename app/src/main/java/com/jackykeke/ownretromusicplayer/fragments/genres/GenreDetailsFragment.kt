package com.jackykeke.ownretromusicplayer.fragments.genres

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialSharedAxis
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.databinding.FragmentPlaylistDetailBinding
import com.jackykeke.ownretromusicplayer.fragments.base.AbsMainActivityFragment
import com.jackykeke.ownretromusicplayer.model.Genre
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

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(requireActivity(), ArrayList(), R.layout.item_list)
        binding.recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

}