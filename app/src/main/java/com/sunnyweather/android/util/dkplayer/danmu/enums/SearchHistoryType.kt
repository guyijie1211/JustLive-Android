package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/10/13.
 */

enum class SearchHistoryType(val value: Int) {
    ANIME(1),

    MAGNET(2);

    companion object {
        fun formValue(value: Int): SearchHistoryType {
            return when (value) {
                1 -> ANIME
                2 -> MAGNET
                else -> ANIME
            }
        }
    }
}