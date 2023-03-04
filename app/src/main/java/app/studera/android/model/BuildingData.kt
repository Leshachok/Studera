package app.studera.android.model

import com.google.android.gms.maps.model.LatLng

data class BuildingData (
    val location: LatLng,
    val lessons: List<Lesson>
)