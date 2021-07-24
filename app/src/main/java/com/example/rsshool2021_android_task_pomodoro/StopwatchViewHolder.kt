package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

var remainingTime: Long = 0


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,
) : RecyclerView.ViewHolder(binding.root) {


    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        binding.customView.setPeriod(stopwatch.initial)
        binding.customView.setCurrent(stopwatch.progress)
        decor(stopwatch)

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
                    stopwatch.progress,
                    stopwatch.color
                )
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            remainingTime = -1
            stopwatch.color = R.color.white
            decor(stopwatch)
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        stopwatch.color = R.color.white
        decor(stopwatch)
        binding.startPauseButton.text = resources.getString(R.string.stop)
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        timer?.cancel()
        stopwatch.isStarted = false
        binding.startPauseButton.text = resources.getString(R.string.start)
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun decor(stopwatch: Stopwatch) {
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
        binding.panel.setBackgroundColor(resources.getColor(stopwatch.color, resources.newTheme()))
        binding.deleteButton.setBackgroundColor(resources.getColor(stopwatch.color, resources.newTheme()))
    }


    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {

        binding.customView.setPeriod(stopwatch.initial)

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {

                stopwatch.currentMs -= interval
                remainingTime = stopwatch.currentMs

                stopwatch.progress += INTERVAL

                binding.customView.setCurrent(stopwatch.progress)
                if (stopwatch.currentMs <= 0L) {
                    onFinish()
                }
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                stopwatch.color = R.color.teal_700
                timer?.cancel()
                binding.stopwatchTimer.text = stopwatch.initial.displayTime()
                stopwatch.currentMs = stopwatch.initial
                decor(stopwatch)
                stopTimer(stopwatch)
            }

            private fun decor(stopwatch: Stopwatch) {
                binding.blinkingIndicator.isInvisible = true
                (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
                binding.panel.setBackgroundColor(resources.getColor(stopwatch.color, resources.newTheme()))
                binding.deleteButton.setBackgroundColor(resources.getColor(stopwatch.color, resources.newTheme()))
            }
        }
    }

    private companion object {
        private const val UNIT_TEN_MS = 100L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day
        private const val INTERVAL = 100L
    }

}