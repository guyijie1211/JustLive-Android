package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/11/4.
 */

enum class SurfaceType(val value: Int) {
    VIEW_SURFACE(1),

    VIEW_TEXTURE(2);

    companion object {
        fun valueOf(value: Int): SurfaceType {
            return when (value) {
                1 -> VIEW_SURFACE
                2 -> VIEW_TEXTURE
                else -> VIEW_TEXTURE
            }
        }
    }
}