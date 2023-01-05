package com.jackykeke.ownretromusicplayer.fragments.playlists

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.adapter.song.OrderablePlaylistSongAdapter
import com.jackykeke.ownretromusicplayer.databinding.FragmentPlaylistDetailBinding
import com.jackykeke.ownretromusicplayer.db.PlaylistWithSongs
import com.jackykeke.ownretromusicplayer.db.toSongs
import com.jackykeke.ownretromusicplayer.extensions.surfaceColor
import com.jackykeke.ownretromusicplayer.fragments.base.AbsMainActivityFragment
import com.jackykeke.ownretromusicplayer.helper.menu.PlaylistMenuHelper
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.ThemedFastScroller
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 *
 * @author keyuliang on 2023/1/4.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PlaylistDetailsFragment : AbsMainActivityFragment(R.layout.fragment_playlist_detail) {

    private val arguments by navArgs<PlaylistDetailsFragmentArgs>()
    private val viewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(arguments.extraPlaylist)
    }


    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlist: PlaylistWithSongs
    private lateinit var playlistSongAdapter: OrderablePlaylistSongAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true).apply {
            drawingViewId = R.id.fragment_container
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor())
            setPathMotion(MaterialArcMotion())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistDetailBinding.bind(view)
        /*提供沿轴共享运动的androidx.transition.Visibility转换。
            当沿X轴配置时，此过渡在出现时在目标中滑动和淡入淡出，在消失时在目标中滑动和淡出。
            当沿Y轴配置时，此过渡在出现时在目标中滑动和淡入淡出，在消失时在目标中滑动和淡出。
            当沿Z轴配置时，此过渡会在目标出现时缩放并淡入，并在目标消失时缩放并淡出。
            滑动或缩放的方向由构造函数的 forward 属性决定。
            为真时，目标将在 X 轴上向左滑动，在 Y 轴上向上滑动，
            并在 Z 轴上向外滑动。
            为 false 时，目标将在 X 轴上向右滑动，在 Y 轴上向下滑动，并在 Z 轴上滑动。
            请注意，这与目标是出现还是消失无关。
            MaterialSharedAxis 支持基于主题的缓动和持续时间。过渡将在运行之前从SceneRoot的上下文加载主题值，并且仅在尚未在过渡实例上设置相应属性时才使用它们。*/
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        mainActivity.setSupportActionBar(binding.toolbar)
        binding.container.transitionName = "playlist"
        playlist = arguments.extraPlaylist
        binding.toolbar.title = playlist.playlistEntity.playlistName
        binding.toolbar.subtitle = MusicUtil.getPlaylistInfoString(requireContext(),playlist.songs.toSongs())

        setUpRecyclerView()

        viewModel.getSongs().observe(viewLifecycleOwner){
            songs(it.toSongs())
        }

        viewModel.playlistExists().observe(viewLifecycleOwner){
            if (!it){
                //尝试在导航层次结构中向上导航。适用于当用户按下应用程序 UI 左上角（或开始）角标有左（或开始）箭头的“向上”按钮时。
                //当用户未从应用程序自己的任务到达当前目的地时，Up 的预期行为不同于Back 。
                // 例如，如果用户在用户单击链接的另一个应用程序任务上托管的活动中查看当前应用程序中的文档或链接。
                // 在这种情况下，当前活动（由用于创建此 NavController 的上下文确定）将完成，用户将根据自己的任务被带到此应用程序中的适当目的地。
                findNavController().navigateUp()
            }
        }

        //推迟进入 Fragment 过渡，直到startPostponedEnterTransition()或FragmentManager.executePendingTransactions()被调用。
        //此方法使 Fragment 能够延迟 Fragment 动画，直到加载所有数据。在此之前，添加、显示和附加的片段将是不可见的，
        // 而移除、隐藏和分离的片段将不会移除其视图。当事务中所有推迟添加的 Fragment 都调用startPostponedEnterTransition()时，事务就会运行。
        //此方法应在添加到 FragmentTransaction 之前或在onCreate(Bundle) 、 onAttach(Context)或onCreateView(LayoutInflater, ViewGroup, Bundle) } 中调用。
        // 必须调用startPostponedEnterTransition()以允许 Fragment 开始转换。
        //当启动可能影响延迟的 FragmentTransaction 的 FragmentTransaction 时，根据其操作中的容器，延迟的 FragmentTransaction 将触发其启动。
        // 提前触发可能会导致延迟交易中的动画错误或不存在。只在独立容器上运行的FragmentTransactions不会互相干扰延期。
        //在具有空视图的片段上调用 ​​postponeEnterTransition 不会推迟转换。
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
    }

    private fun setUpRecyclerView() {
         playlistSongAdapter = OrderablePlaylistSongAdapter(
             playlist.playlistEntity,
             requireActivity(),
             ArrayList(),
             R.layout.item_queue
         )

        val dragDropManager = RecyclerViewDragDropManager()

        val wrappedAdapter : RecyclerView.Adapter<*> = dragDropManager.createWrappedAdapter(playlistSongAdapter)

        val animator: GeneralItemAnimator = DraggableItemAnimator()
        binding.recyclerView.itemAnimator = animator

        dragDropManager.attachRecyclerView(binding.recyclerView)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = wrappedAdapter
            ThemedFastScroller.create(this)
        }
        playlistSongAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }
        })
    }


    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlist_detail, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return PlaylistMenuHelper.handleMenuClick(requireActivity(), playlist, item)
    }

    private fun checkIsEmpty() {
        binding.empty.isVisible = playlistSongAdapter.itemCount == 0
        binding.emptyText.isVisible = playlistSongAdapter.itemCount == 0
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        playlistSongAdapter.saveSongs(playlist.playlistEntity)
        super.onPause()
    }

    private fun showEmptyView() {
        binding.empty.isVisible = true
        binding.emptyText.isVisible = true
    }

    fun songs(songs: List<Song>) {
        binding.progressIndicator.hide()
        if (songs.isNotEmpty()) {
            playlistSongAdapter.swapDataSet(songs)
        } else {
            showEmptyView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}