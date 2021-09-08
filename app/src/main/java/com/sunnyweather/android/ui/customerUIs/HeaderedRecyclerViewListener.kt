/*
 * Copyright 2017 Paul Rybitskyi, paul.rybitskyi.work@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunnyweather.android.ui.customerUIs

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.roundToInt

internal abstract class HeaderedRecyclerViewListener(context: Context) : RecyclerView.OnScrollListener() {


    companion object {

        private const val SWIPE_DETECTION_DISTANCE_IN_DP = 10

    }


    private var scrollDetectionDistance = SWIPE_DETECTION_DISTANCE_IN_DP.dpToPx(context)

    private var firstVisiblePosition = 0
    private var previousFirstVisiblePosition = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if(dy > 0) {
            onScrolledDownwards(recyclerView, dy)
        } else if(dy < 0) {
            onScrolledUpwards(recyclerView, dy)
        }
    }


    private fun onScrolledUpwards(recyclerView: RecyclerView, deltaY: Int) {
        firstVisiblePosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0))
        val isFirstItem = (firstVisiblePosition == 0)

        if(shouldShowHeader(deltaY, isFirstItem)) {
            showHeader()
        }

        previousFirstVisiblePosition = firstVisiblePosition
    }


    private fun shouldShowHeader(deltaY: Int, isFirstItem: Boolean): Boolean {
        return (
            (abs(deltaY) >= scrollDetectionDistance) ||
            (isFirstItem && (firstVisiblePosition != previousFirstVisiblePosition))
        )
    }


    private fun onScrolledDownwards(recyclerView: RecyclerView, deltaY: Int) {
        firstVisiblePosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0))

        if(shouldHideHeader(deltaY)) {
            hideHeader()
        }

        previousFirstVisiblePosition = firstVisiblePosition
    }


    private fun shouldHideHeader(deltaY: Int): Boolean {
        return (
            (firstVisiblePosition > 0) &&
            ((abs(deltaY) >= scrollDetectionDistance) || (firstVisiblePosition > previousFirstVisiblePosition))
        )
    }


    open fun showHeader() {

    }


    open fun hideHeader() {

    }
    fun Int.dpToPx(context: Context): Int {
        return toFloat().dpToPx(context).roundToInt()
    }
    fun Float.dpToPx(context: Context): Float {
        return (this * context.resources.displayMetrics.density)
    }
}