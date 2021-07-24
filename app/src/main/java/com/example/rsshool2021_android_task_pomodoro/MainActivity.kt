package com.example.rsshool2021_android_task_pomodoro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import kotlin.system.exitProcess



class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private var back_pressed: Long = 0

    private val stopwatches = mutableListOf<Stopwatch>()
    private val stopwatchAdapter = StopwatchAdapter(this, stopwatches)
    private var nextId = 0
    var startTimerLong = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (savedInstanceState != null) {
            nextId = savedInstanceState.getInt(NEXT_ID)
            val size = savedInstanceState.getInt(SIZE_TIMERS)
            for (i in 0 until size) {
                val id = savedInstanceState.getInt("$ID$i")
                val initial = savedInstanceState.getLong("$INITIAL$i")
                val currentMs = savedInstanceState.getLong("$MS$i")
                val progress = savedInstanceState.getLong("$PROGRESS$i")
                val isStarted = savedInstanceState.getBoolean("$START$i")
                val color = savedInstanceState.getInt("$COLOR$i")
                stopwatches.add(Stopwatch(id, initial, currentMs, progress, isStarted, color))
            }
            stopwatchAdapter.submitList(stopwatches.toList())
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            stopwatchAdapter.setHasStableIds(true)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            val startTimer = binding.quantity.text.toString()
            if (startTimer.isNotBlank() && startTimer.toLong() < 1441) {
                startTimerLong = startTimer.toLong() * 60000L
                stopwatches.add(
                    Stopwatch(
                        nextId++,
                        startTimerLong,
                        startTimerLong,
                        0,
                        false,
                        R.color.white
                    )
                )
                stopwatchAdapter.submitList(stopwatches.toList())

            } else Toast.makeText(this, "Введенны не коректные данные!!!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NEXT_ID, nextId)
        outState.putInt(SIZE_TIMERS, stopwatches.size)
        for (i in stopwatches.indices) {
            outState.putInt("$ID$i", stopwatches[i].id)
            outState.putLong("$INITIAL$i", stopwatches[i].initial)
            outState.putLong("$MS$i", stopwatches[i].currentMs)
            outState.putLong("$PROGRESS$i", stopwatches[i].progress)
            outState.putBoolean("$START$i", stopwatches[i].isStarted)
            outState.putInt("$COLOR$i", stopwatches[i].color)
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, null, null, true)
    }

    override fun stop(id: Int, initial: Long, currentMs: Long, progress: Long, color: Int) {
        changeStopwatch(id, initial, currentMs, progress, false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(
        id: Int,
        initial: Long?,
        currentMs: Long?,
        progress: Long?,
        isStarted: Boolean,
    ) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id != id) {
                newTimers.add(
                    Stopwatch(
                        it.id,
                        it.initial,
                        it.currentMs,
                        it.progress,
                        isStarted = false,
                        it.color
                    )
                )
            } else
                if (it.id == id) {
                    newTimers.add(
                        Stopwatch(
                            it.id,
                            initial ?: it.initial,
                            currentMs ?: it.currentMs,
                            progress ?: it.progress,
                            isStarted,
                            it.color
                        )
                    )
                } else {
                    newTimers.add(it)
                }
        }

        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val time = stopwatches.find { it.isStarted }
        if (time != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, remainingTime)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    private companion object {
        private const val NEXT_ID = "NEXT_ID"
        private const val SIZE_TIMERS = "SIZE_TIMERS"
        private const val ID = "ID"
        private const val MS = "MS"
        private const val INITIAL = "INITIAL"
        private const val PROGRESS = "PROGRESS"
        private const val START = "START"
        private const val COLOR = "COLOR"
    }

    override fun onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {

            super.onBackPressed()
            exitProcess(0)
        } else {
            Toast.makeText(baseContext, "Нажмите дважды, чтобы выйти!", Toast.LENGTH_SHORT).show()
        }
        back_pressed = System.currentTimeMillis()
    }

}
