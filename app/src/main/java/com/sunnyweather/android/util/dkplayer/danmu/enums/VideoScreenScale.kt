package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/11/1.
 */

enum class VideoScreenScale(val value: Int) {
    //默认类型
    SCREEN_SCALE_DEFAULT(0),

    //16：9比例类型，最为常见
    SCREEN_SCALE_16_9(1),

    //4：3比例类型，也比较常见
    SCREEN_SCALE_4_3(2),

    //充满整个控件视图
    SCREEN_SCALE_MATCH_PARENT(3),

    //原始类型，指视频的原始类型
    SCREEN_SCALE_ORIGINAL(4),

    //剧中裁剪类型
    SCREEN_SCALE_CENTER_CROP(5);

    companion object {
        fun formValue(value: Int): VideoScreenScale {
            return when (value) {
                0 -> SCREEN_SCALE_DEFAULT
                1 -> SCREEN_SCALE_16_9
                2 -> SCREEN_SCALE_4_3
                3 -> SCREEN_SCALE_MATCH_PARENT
                4 -> SCREEN_SCALE_ORIGINAL
                5 -> SCREEN_SCALE_CENTER_CROP
                else -> SCREEN_SCALE_DEFAULT
            }
        }
    }
}