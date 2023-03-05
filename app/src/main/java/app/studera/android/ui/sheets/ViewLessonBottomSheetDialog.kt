package app.studera.android.ui.sheets

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import app.studera.android.R
import app.studera.android.databinding.BottomsheetLessonViewBinding
import app.studera.android.model.Lesson
import app.studera.android.util.LessonType
import app.studera.android.util.formatTime
import app.studera.android.util.zonedDateTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ViewLessonBottomSheetDialog(private val lesson: Lesson)
    : BottomSheetDialogFragment(R.layout.bottomsheet_lesson_view) {

    private var _binding: BottomsheetLessonViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetLessonViewBinding.inflate(layoutInflater)

        binding.textViewLessonTitle.text = lesson.title

        fillDate()
        fillType()

        binding.button.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
    private fun fillDate() {
        val start = lesson.start.zonedDateTime()
        val end = lesson.end.zonedDateTime()

        val timeRange = "${start.hour}:${start.minute.formatTime()} - ${end.hour}:${end.minute.formatTime()}"

        binding.textViewLessonTime.text = timeRange
    }

    private fun fillType() {
        val (chipBackroundColor, chipText) = when(lesson.type){
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
        val bgColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), chipBackroundColor))

        binding.chipLessonType.apply {
            text = chipText
            chipBackgroundColor = bgColor
        }
        binding.chipLecturer.text = lesson.lecturer
    }
}