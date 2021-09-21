package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/10/13.
 */

enum class AnimeSortType(val value: String) {
    NAME("sort_by_name"),

    FOLLOW("sort_by_follow"),

    RATING("sort_by_rating"),

    DATE("sort_by_date"),

    NONE("");

    companion object {
        fun formValue(value: String): AnimeSortType {
            return when (value) {
                "sort_by_name" -> NAME
                "sort_by_follow" -> FOLLOW
                "sort_by_rating" -> RATING
                "sort_by_date" -> DATE
                else -> NONE
            }
        }
    }
}