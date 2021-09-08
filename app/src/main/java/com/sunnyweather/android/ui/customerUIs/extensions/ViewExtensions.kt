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

package com.sunnyweather.android.ui.customerUIs.extensions

import android.view.View
import androidx.core.view.isVisible
import com.sunnyweather.android.R


internal fun View.setVisibilityMarker(isVisible: Boolean) {
    setTag(R.id.visibility_marker, isVisible)
}


internal fun View.getVisibilityMarker(): Boolean {
    return ((getTag(R.id.visibility_marker) as? Boolean) ?: isVisible)
}


internal fun View.setAnimationMarker(marker: Any) {
    setTag(R.id.animation_marker, marker)
}


@Suppress("UNCHECKED_CAST")
internal fun <T> View.getAnimationMarker(): T? {
    return (getTag(R.id.animation_marker) as T?)
}


internal fun View.cancelAllAnimations() {
    this.clearAnimation()
    this.animate().cancel()
}