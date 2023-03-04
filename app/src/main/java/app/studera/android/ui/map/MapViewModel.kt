package app.studera.android.ui.map

import androidx.lifecycle.ViewModel
import app.studera.android.data.EducationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class MapViewModel : ViewModel() {

    val repository = EducationRepository.I

    private val buildingsFlow = MutableStateFlow<LocalDate>(LocalDate.now())

    val lessonsFlow = buildingsFlow.flatMapLatest { date ->
        val lessons = repository.getLessons(date)
        flowOf(lessons)
    }

}