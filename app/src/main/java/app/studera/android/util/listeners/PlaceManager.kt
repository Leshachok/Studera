package app.studera.android.util.listeners

import com.google.android.gms.maps.model.Marker

interface PlaceManager {

    fun onIconChanged(drawable: Int)

    fun onTextAdded(text: String)
}