package com.example.rsshool2021_android_task_pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

class StopwatchAdapter(private val listener: StopwatchListener, private val stopwatches: MutableList<Stopwatch>) :
    ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)
        return StopwatchViewHolder(binding, listener, binding.root.context.resources)
    }


    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return stopwatches[position].id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return stopwatches[position].id
    }




    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {

                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted &&
                        oldItem.initial == newItem.initial &&
                        oldItem.progress == newItem.progress &&
                        oldItem.color == newItem.color
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) = Any()
        }
    }

}