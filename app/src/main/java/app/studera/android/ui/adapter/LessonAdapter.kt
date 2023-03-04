package app.studera.android.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.studera.android.R
import app.studera.android.model.Lesson
import app.studera.android.util.LessonType
import app.studera.android.util.formatTime
import app.studera.android.util.listeners.LessonManager
import app.studera.android.util.zonedDateTime
import com.google.android.material.chip.Chip

class LessonAdapter(private val items: List<Lesson>, private val context: Context, private val lessonManager: LessonManager) : RecyclerView.Adapter<LessonAdapter.EventViewHolder>() {

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val timeTextView: TextView
        val typeChip: Chip

        init {
            timeTextView = view.findViewById(R.id.textViewLessonTime)
            titleTextView = view.findViewById(R.id.textViewLessonTitle)
            typeChip = view.findViewById(R.id.chipLessonType)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_lesson, parent, false)

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = items[position]
        val (chipBackroundColor, text) = when(item.type){
            LessonType.LECTURE -> {
                R.color.color_blue01 to "Лекція"
            }
            LessonType.LAB -> {
                R.color.color_green01 to "Лабораторна"
            }
            LessonType.PRACTICE -> {
                R.color.color_red01 to "Практика"
            }
        }
        val bgColor = ColorStateList.valueOf(ContextCompat.getColor(context, chipBackroundColor))

        val start = item.start.zonedDateTime()
        val end = item.end.zonedDateTime()

        val timeRange = "${start.hour}:${start.minute.formatTime()} - ${end.hour}:${end.minute.formatTime()}"

        holder.apply {
            titleTextView.text = item.title
            timeTextView.text = timeRange
            typeChip.text = text
            typeChip.chipBackgroundColor = bgColor
        }

        holder.itemView.setOnClickListener {
            lessonManager.onLessonClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
