package com.xyoye.data_component.enums

/**
 * Created by xyoye on 2020/11/1.
 */

enum class PlayState(val value: Int) {
    //播放错误
    STATE_ERROR(-1),

    //播放未开始，即将进行
    STATE_IDLE(0),

    //播放准备中
    STATE_PREPARING(1),

    //播放准备就绪
    STATE_PREPARED(2),

    //正在播放
    STATE_PLAYING(3),

    //暂停播放
    STATE_PAUSED(4),

    //缓冲结束，开始播放
    STATE_BUFFERING_PLAYING(5),

    //缓冲开始，暂停播放
    STATE_BUFFERING_PAUSED(6),

    //播放完成
    STATE_COMPLETED(7),

    //开始播放中止
    STATE_START_ABORT(8);

    companion object {
        fun formValue(value: Int): PlayState {
            return when (value) {
                -1 -> STATE_ERROR
                0 -> STATE_IDLE
                1 -> STATE_PREPARING
                2 -> STATE_PREPARED
                3 -> STATE_PLAYING
                4 -> STATE_PAUSED
                5 -> STATE_BUFFERING_PLAYING
                6 -> STATE_BUFFERING_PAUSED
                7 -> STATE_COMPLETED
                8 -> STATE_START_ABORT
                else -> STATE_ERROR
            }
        }
    }
}