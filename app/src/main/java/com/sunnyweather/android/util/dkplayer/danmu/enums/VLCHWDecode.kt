package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/9/7.
 */

enum class VLCHWDecode(val value: Int) {

    HW_ACCELERATION_AUTO(-1),

    HW_ACCELERATION_DISABLE(0),

    HW_ACCELERATION_DECODING(1),

    HW_ACCELERATION_FULL(2);

    companion object {
        fun valueOf(value: Int): VLCHWDecode {
            return when (value) {
                -1 -> HW_ACCELERATION_AUTO
                0 -> HW_ACCELERATION_DISABLE
                1 -> HW_ACCELERATION_DECODING
                2 -> HW_ACCELERATION_FULL
                else -> HW_ACCELERATION_AUTO
            }
        }
    }
}