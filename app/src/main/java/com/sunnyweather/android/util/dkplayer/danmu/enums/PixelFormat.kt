package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/9/7.
 */

enum class PixelFormat(val value: String) {
    PIXEL_AUTO(""),

    PIXEL_RGB565("fcc-rv16"),

    PIXEL_RGB888("fcc-rv24"),

    PIXEL_RGBX8888("fcc-rv32"),

    PIXEL_YV12("fcc-yv12"),

    PIXEL_OPEN_GL_ES2("fcc-_es2");

    companion object {
        fun valueOf(value: String?): PixelFormat {
            return when (value) {
                "fcc-rv16" -> PIXEL_RGB565
                "fcc-rv24" -> PIXEL_RGB888
                "fcc-rv32" -> PIXEL_RGBX8888
                "fcc-yv12" -> PIXEL_YV12
                "fcc-_es2" -> PIXEL_OPEN_GL_ES2
                "" -> PIXEL_AUTO
                else -> PIXEL_AUTO
            }
        }
    }
}