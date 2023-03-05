package app.studera.android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import app.studera.android.R
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context) : InfoWindowAdapter {

    var binding: View = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private fun setInfoWindowText(marker: Marker) {
        val title = marker.title

        val textView = binding.findViewById<TextView>(R.id.textView)
        textView.text = title
    }

    override fun getInfoContents(p0: Marker): View? {
        setInfoWindowText(p0)
        return binding
    }

    override fun getInfoWindow(p0: Marker): View? {
        setInfoWindowText(p0)
        return binding
    }
}