package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var remainingTime: Long = 0


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {


    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        binding.customView.setPeriod(stopwatch.initial)
        binding.customView.setCurrent(stopwatch.progress)
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(
                    stopwatch.id,
                    stopwatch.initial,
                    stopwatch.currentMs,
                    stopwatch.progress
                )
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }

        binding.deleteButton.setOnClickListener {
            decor()
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        decor()
        binding.startPauseButton.text = "stop"
//        stopwatch.isStarted = true
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        timer?.cancel()
        stopwatch.isStarted = false
        binding.startPauseButton.text = "start"
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun decor() {
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
        binding.panel.setBackgroundColor(resources.getColor(R.color.white))
        binding.restartButton.setBackgroundColor(resources.getColor(R.color.white))
        binding.deleteButton.setBackgroundColor(resources.getColor(R.color.white))
    }


    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {

        binding.customView.setPeriod(stopwatch.initial)

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {

                stopwatch.currentMs -= interval
                remainingTime = stopwatch.currentMs

                stopwatch.progress += INTERVAL
//                Log.d("initial", stopwatch.initial.toString())
//                Log.d("currentMs", stopwatch.currentMs.toString())
//                Log.d("progress", stopwatch.progress.toString())
                Log.d("progress", timer.toString())
                binding.customView.setCurrent(stopwatch.progress)

                if (stopwatch.currentMs <= 0L) {
                    onFinish()
                    return
                }
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                timer?.cancel()
                binding.stopwatchTimer.text = stopwatch.initial.displayTime()
                stopwatch.currentMs = stopwatch.initial
                stopTimer(stopwatch)
                decor()
            }

            private fun decor() {
                binding.blinkingIndicator.isInvisible = true
                (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
                binding.panel.setBackgroundColor(resources.getColor(R.color.teal_700))
                binding.restartButton.setBackgroundColor(resources.getColor(R.color.teal_700))
                binding.deleteButton.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
        }
    }

    private companion object {
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day
        private const val INTERVAL = 1000L
    }

}