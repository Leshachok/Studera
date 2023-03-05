package app.studera.android.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.studera.android.data.EducationRepository
import app.studera.android.model.BuildingData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate

class MapViewModel : ViewModel() {

    private val repository = EducationRepository.I

    private val dateFlow = MutableSharedFlow<LocalDate?>()

    val dateLiveData = dateFlow.asLiveData()

    val buildingsFlow = dateFlow.flatMapLatest { date ->
        if(date == null) {
            val buildings = repository.getBuildings().map {
                BuildingData(it, listOf())
            }
            flowOf(buildings)
        }
        else {
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

    fun setDate(today: Boolean = true){
        viewModelScope.launch {
            if(today){
                val date = LocalDate.now()
                dateFlow.emit(date)
            }
            else dateFlow.emit(null)
        }
    }

}