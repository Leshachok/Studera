package app.studera.android.ui.timetable

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.studera.android.R
import app.studera.android.databinding.CalendarItemDayBinding
import app.studera.android.databinding.FragmentTimetableBinding
import app.studera.android.model.Lesson
import app.studera.android.ui.adapter.LessonAdapter
import app.studera.android.ui.sheets.ViewLessonBottomSheetDialog
import app.studera.android.util.displayText
import app.studera.android.util.listeners.LessonManager
import app.studera.android.util.setTextColorRes
import app.studera.android.util.ukDaysOfWeek
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class TimetableFragment : Fragment(), LessonManager {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimetableViewModel by viewModels()

    private var lessons = mutableListOf<Lesson>()
    private lateinit var lessonAdapter: LessonAdapter

    private var isInWeekMode = true
        set(value) {
            field = value
            toogleCalendarModeView()
        }

    private val monthCalendarView: CalendarView get() = binding.exOneCalendar
    private val weekCalendarView: WeekCalendarView get() = binding.exOneWeekCalendar

    private var selectedDate: LocalDate? = LocalDate.now()
    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)

        val daysOfWeek = ukDaysOfWeek()

        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
            }


        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
        setupWeekCalendar(startMonth, endMonth, currentMonth, daysOfWeek)

        monthCalendarView.isInvisible = isInWeekMode
        weekCalendarView.isInvisible = !isInWeekMode

        val layoutParams = monthCalendarView.layoutParams as FrameLayout.LayoutParams
        val height = weekCalendarView.height
        layoutParams.height = height
        monthCalendarView.layoutParams = layoutParams

        binding.checkboxCalendarTypeSwitch.setOnCheckedChangeListener { _, b ->
            isInWeekMode = !b
        }

        lessonAdapter = LessonAdapter(lessons, requireContext(), this)

        binding.recyclerView.apply {
            adapter = lessonAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            viewModel.lessonsFlow.collectLatest {
                lessons.clear()
                lessons.addAll(it)

                lessonAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarItemDayBinding.bind(view).exOneDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
            }
        }
        monthCalendarView.monthScrollListener = { updateTitle() }
        monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun setupWeekCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class WeekDayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: WeekDay
            val textView = CalendarItemDayBinding.bind(view).exOneDayText

            init {
                view.setOnClickListener {
                    if (day.position == WeekDayPosition.RangeDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == WeekDayPosition.RangeDate)
            }
        }
        weekCalendarView.weekScrollListener = { updateTitle() }
        weekCalendarView.setup(
            startMonth.atStartOfMonth(),
            endMonth.atEndOfMonth(),
            daysOfWeek.first(),
        )
        weekCalendarView.scrollToWeek(today)
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
            when (date) {
                selectedDate -> {
                    textView.setTextColorRes(R.color.white)
                    textView.setBackgroundResource(R.drawable.calendar_day_selected_bg)
                }
                else -> {
                    textView.setTextColorRes(R.color.color_grey02)
                    textView.setBackgroundResource(R.drawable.calendar_day_unselected_bg)
                }
            }
        } else {
            textView.setTextColorRes(R.color.transparent)
            textView.background = null
        }
    }

    private fun dateClicked(date: LocalDate) {
        viewModel.setDate(date)
        if (date == selectedDate) {
            selectedDate = null
            monthCalendarView.notifyDateChanged(date)
            weekCalendarView.notifyDateChanged(date)
        } else {
            val oldDate = selectedDate
            selectedDate = date
            monthCalendarView.notifyDateChanged(date)
            weekCalendarView.notifyDateChanged(date)
            oldDate?.let {
                monthCalendarView.notifyDateChanged(it)
                weekCalendarView.notifyDateChanged(it)
            }

        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val isMonthMode = !isInWeekMode
        if (isMonthMode) {
            val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
            binding.topAppBar.title =
                month.month.displayText(short = false)
        } else {
            val week = weekCalendarView.findFirstVisibleWeek() ?: return
            // In week mode, we show the header a bit differently because
            // an index can contain dates from different months/years.
            val firstDate = week.days.first().date
            val lastDate = week.days.last().date
            if (firstDate.yearMonth == lastDate.yearMonth) {
                binding.topAppBar.title = firstDate.month.displayText(short = false)
            } else {
                binding.topAppBar.title =
                    firstDate.month.displayText(short = false) + " - " +
                            lastDate.month.displayText(short = false)
            }
        }
    }

    private fun toogleCalendarModeView() {
        // We want the first visible day to remain visible after the
        // change so we scroll to the position on the target calendar.
        if (isInWeekMode) {
            val targetDate = monthCalendarView.findFirstVisibleDay()?.date ?: return
            weekCalendarView.scrollToWeek(selectedDate ?: targetDate)
        } else {
            // It is possible to have two months in the visible week (30 | 31 | 1 | 2 | 3 | 4 | 5)
            // We always choose the second one. Please use what works best for your use case.
            val targetMonth = weekCalendarView.findLastVisibleDay()?.date?.yearMonth ?: return
            monthCalendarView.scrollToMonth(targetMonth)
        }

        val weekHeight = weekCalendarView.height
        // If OutDateStyle is EndOfGrid, you could simply multiply weekHeight by 6.
        val visibleMonthHeight = weekHeight *
                monthCalendarView.findFirstVisibleMonth()?.weekDays.orEmpty().count()

        val oldHeight = if (isInWeekMode) visibleMonthHeight else weekHeight
        val newHeight = if (isInWeekMode) weekHeight else visibleMonthHeight

        // Animate calendar height changes.
        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { anim ->
            monthCalendarView.updateLayoutParams {
                height = anim.animatedValue as Int
            }
            // A bug is causing the month calendar to not redraw its children
            // with the updated height during animation, this is a workaround.
            monthCalendarView.children.forEach { child ->
                child.requestLayout()
            }
        }

        animator.doOnStart {
            if (!isInWeekMode) {
                weekCalendarView.isInvisible = true
                monthCalendarView.isVisible = true
            }
        }
        animator.doOnEnd {
            if (isInWeekMode) {
                weekCalendarView.isVisible = true
                monthCalendarView.isInvisible = true
            } else {
                // Allow the month calendar to be able to expand to 6-week months
                // in case we animated using the height of a visible 5-week month.
                // Not needed if OutDateStyle is EndOfGrid.
                monthCalendarView.updateLayoutParams {
                    height =
                        ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            updateTitle()
        }
        animator.duration = 250
        animator.start()
    }

    override fun onLessonClicked(lesson: Lesson) {
        val dialog = ViewLessonBottomSheetDialog(lesson)
        dialog.show(parentFragmentManager, "lesson")
    }
}