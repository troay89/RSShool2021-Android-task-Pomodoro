package com.example.rsshool2021_android_task_pomodoro

import androidx.annotation.ColorRes

data class Stopwatch(
    val id: Int,
    val initial: Long,
    var currentMs: Long,
    var progress: Long,
    var isStarted: Boolean,
    @ColorRes
    var color: Int = R.color.white
)
