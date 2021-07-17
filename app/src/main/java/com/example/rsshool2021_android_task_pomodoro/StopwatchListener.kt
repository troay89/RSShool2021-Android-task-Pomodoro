package com.example.rsshool2021_android_task_pomodoro

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, initial: Long, currentMs: Long, progress: Long)

    fun reset(id: Int)

    fun delete(id: Int)

}