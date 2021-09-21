package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/10/26.
 */

enum class MagnetScreenType(val value: Int) {
    SUBGROUP(1),

    TYPE(2);

    companion object {
        fun valueOf(value: Int): MagnetScreenType {
            return when (value) {
                1 -> SUBGROUP
                2 -> TYPE
                else -> SUBGROUP
            }
        }
    }
}