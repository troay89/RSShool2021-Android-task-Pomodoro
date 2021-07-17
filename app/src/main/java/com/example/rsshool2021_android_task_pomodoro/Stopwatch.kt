package com.example.rsshool2021_android_task_pomodoro

data class Stopwatch(
    val id: Int,
    val initial: Long,
    var currentMs: Long,
    var progress: Long,
    var isStarted: Boolean
)
