package app.studera.android.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.studera.android.R
import app.studera.android.databinding.FragmentMapBinding
import app.studera.android.ui.sheets.PlaceCreateBottomSheetDialog
import app.studera.android.util.listeners.PlaceManager
import app.studera.android.util.localHM
import app.studera.android.util.toPx
import app.studera.android.util.zonedDateTime
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback, PlaceManager {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private val markers = mutableListOf<MarkerOptions>()

    private var isMapReady = false

    private var newMarker: Marker? = null
    private var isInSandBoxMode = false
        set(value) {
            field = value
            binding.button.text = if(value) "Додати" else "Додати нову точку"
            binding.marker.visibility = if(value) View.VISIBLE else View.INVISIBLE
            binding.close.visibility = if(value) View.VISIBLE else View.INVISIBLE
        }

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

        binding.button.setOnClickListener {
            if(!isMapReady) return@setOnClickListener
            isInSandBoxMode = if(!isInSandBoxMode){
                true
            } else{
                drawNewMarker()
                openBottomSheet()
                false
            }
        }

        binding.close.setOnClickListener {
            isInSandBoxMode = false
        }


        return binding.root
    }

    private fun drawNewMarker() {
        val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_custom_place)
        val resized = Bitmap.createScaledBitmap(bitmap, (40).toPx, (59).toPx, false)
        newMarker = map.addMarker(
            MarkerOptions()
                .position(map.cameraPosition.target)
                .icon(BitmapDescriptorFactory.fromBitmap(resized))
        )
    }

    private fun openBottomSheet() {
        val dialog = PlaceCreateBottomSheetDialog(this)
        dialog.show(parentFragmentManager, "place")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        isMapReady = true

        markers.forEach { map.addMarker(it) }

        val latLng = LatLng(46.460054, 30.751629)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        map.setOnMarkerClickListener { m ->
            m.showInfoWindow()
            true
        }
    }

    override fun onIconChanged(drawable: Int) {
        val bitmap = BitmapFactory.decodeResource(requireContext().resources, drawable)
        val resized = Bitmap.createScaledBitmap(bitmap, (56).toPx, (70).toPx, false)
        newMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(resized))
    }

    override fun onTextAdded(text: String) {
        newMarker?.title = text
    }
}