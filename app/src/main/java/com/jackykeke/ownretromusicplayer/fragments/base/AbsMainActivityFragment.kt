package com.jackykeke.ownretromusicplayer.fragments.base

import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.jackykeke.ownretromusicplayer.activities.MainActivity
import com.jackykeke.ownretromusicplayer.fragments.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 * @author keyuliang on 2022/12/1.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsMainActivityFragment(@LayoutRes layout: Int) : AbsMusicServiceFragment(layout),
    MenuProvider {

    val libraryViewModel: LibraryViewModel by sharedViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost:MenuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.STARTED)
    }

}