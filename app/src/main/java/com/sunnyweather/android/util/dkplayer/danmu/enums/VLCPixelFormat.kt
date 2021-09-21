package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/9/7.
 */

enum class VLCPixelFormat(val value: String) {

    PIXEL_RGB_16("RV16"),

    PIXEL_RGB_32("RV32"),

    PIXEL_YUV("YV12");

    companion object {
        fun valueOf(value: String?): VLCPixelFormat {
            return when (value) {
                "RV16" -> PIXEL_RGB_16
                "RV32" -> PIXEL_RGB_32
                "YV12" -> PIXEL_YUV
                else -> PIXEL_RGB_32
            }
        }
    }
}