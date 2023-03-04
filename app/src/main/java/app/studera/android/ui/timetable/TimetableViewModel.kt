package app.studera.android.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.studera.android.data.EducationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate

class TimetableViewModel : ViewModel() {

    private val repository = EducationRepository.I

    private val lessonDateFlow = MutableStateFlow<LocalDate>(LocalDate.now())

    val lessonsFlow = lessonDateFlow.flatMapLatest { date ->
        val lessons = repository.getLessons(date)
        flowOf(lessons)
    }

    fun setDate(date: LocalDate){
        viewModelScope.launch {
            lessonDateFlow.emit(date)
        }
    }

}