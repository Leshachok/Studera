package app.studera.android.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import app.studera.android.data.EducationRepository
import app.studera.android.model.BuildingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class MapViewModel : ViewModel() {

    private val repository = EducationRepository.I

    private val dateFlow = MutableStateFlow<LocalDate>(LocalDate.now())

    val dateLiveData = dateFlow.asLiveData()

    val buildingsFlow = dateFlow.flatMapLatest { date ->
        val lessons = repository.getLessons(date)
        val buildings = lessons
            .filter { it.building != null }
            .groupBy { it.building!! }
            .entries.map {
                BuildingData(it.key, it.value)
            }

        flowOf(buildings)
    }

}