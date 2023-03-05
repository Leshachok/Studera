package app.studera.android.model

import app.studera.android.util.Building
import com.google.android.gms.maps.model.LatLng

data class BuildingData (
    val building: Building,
    val lessons: List<Lesson>
)