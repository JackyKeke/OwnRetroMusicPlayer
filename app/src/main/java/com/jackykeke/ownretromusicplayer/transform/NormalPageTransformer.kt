/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.jackykeke.ownretromusicplayer.transform

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

/**
 * @author Hemanth S (h4h13).
 */
//普通页面转换器
class NormalPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            val pageHeight = height

            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left. 此页面在屏幕左侧。
                    alpha = 1f
                    scaleY = 0.7f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well   修改默认的幻灯片切换以缩小页面
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        -horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)  缩小页面（在 MIN_SCALE 和 1 之间）
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size. 淡化页面相对于它的大小。
                    //setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 1f
                    scaleY = 0.7f
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }
}
