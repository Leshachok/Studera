package app.studera.android.ui.map

import androidx.lifecycle.ViewModel
import app.studera.android.data.EducationRepository
import app.studera.android.model.BuildingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class MapViewModel : ViewModel() {

    val repository = EducationRepository.I

    private val dateFlow = MutableStateFlow<LocalDate>(LocalDate.now())

    val buildingsFlow = dateFlow.flatMapLatest { date ->
        val lessons = repository.getLessons(date)
        val buildings = lessons
            .filter { it.building != null }
            .groupBy { it.building!! }
            .entries.map {
                val location = it.value.first().building!!.location
                BuildingData(location, it.value)
            }

        flowOf(buildings)
    }

}