package app.studera.android.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.studera.android.R
import app.studera.android.databinding.FragmentMapBinding
import app.studera.android.model.BuildingData
import app.studera.android.util.localHM
import app.studera.android.util.zonedDateTime
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private val buildings = mutableListOf<BuildingData>()

    private val markers = mutableListOf<MarkerOptions>()

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lifecycleScope.launch {
            viewModel.buildingsFlow.collectLatest {
                it.forEach { b ->
                    val title = when(b.lessons.size) {
                        1 -> {
                            val lesson = b.lessons.first()
                            val start = lesson.start.zonedDateTime().localHM()
                            val end = lesson.end.zonedDateTime().localHM()
                            "$start - $end"
                        }
                        else -> {
                            val lessons = b.lessons
                            var text = ""
                            lessons.forEachIndexed { index, lesson,  ->
                                val start = lesson.start.zonedDateTime().localHM()
                                val end = lesson.end.zonedDateTime().localHM()
                                text += "$start - $end"
                            }
                            text += ""
                            text
                        }
                    }
                    markers.add(

                        MarkerOptions()
                            .position(b.location)
                            .title("тут помещается одна строка")
                            .snippet("и тут еще одна")
                    )
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        val latLng = LatLng(46.460054, 30.751629)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        map.setOnMarkerClickListener { m ->
            m.showInfoWindow()
            true
        }

        markers.forEach { map.addMarker(it) }
    }
}