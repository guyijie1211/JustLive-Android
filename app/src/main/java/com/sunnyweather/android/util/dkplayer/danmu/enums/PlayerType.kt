package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/9/7.
 */

enum class PlayerType(val value: Int) {
    TYPE_IJK_PLAYER(1),

    TYPE_EXO_PLAYER(2),

    TYPE_VLC_PLAYER(3);

    companion object {
        fun valueOf(value: Int): PlayerType {
            return when (value) {
                1 -> TYPE_IJK_PLAYER
                2 -> TYPE_EXO_PLAYER
                3 -> TYPE_VLC_PLAYER
                else -> TYPE_IJK_PLAYER
            }
        }
    }
}