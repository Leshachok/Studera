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
import app.studera.android.model.BuildingData
import app.studera.android.ui.adapter.CustomInfoWindowAdapter
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
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, PlaceManager {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private val currentMarkers = mutableListOf<Marker>()

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

    private lateinit var buildingBitmap: Bitmap
    private lateinit var foodBitmap: Bitmap
    private lateinit var beerBitmap: Bitmap
    private lateinit var electricityBitmap: Bitmap
    private lateinit var placeBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBitmaps()
    }

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
                fillUI(it)
            }
        }

        viewModel.dateLiveData.observe(viewLifecycleOwner){
            val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag("uk"))

            val text = it?.format(uaDateFormatter)
            binding.title.text = text?.let {
                "Ваші розклад"
            } ?: run {
                "Карта кампусу"
            }
        }

        binding.button.setOnClickListener {
            if(!isMapReady) return@setOnClickListener
            isInSandBoxMode = if(!isInSandBoxMode){
                true
            } else {
                drawNewMarker()
                openBottomSheet()
                false
            }
        }

        binding.close.setOnClickListener {
            isInSandBoxMode = false
        }

        binding.filter.setOnCheckedChangeListener { _, b ->
            if(b){
                viewModel.setDate()
            }else viewModel.setDate(today = false)
        }

        return binding.root
    }

    private fun loadBitmaps(){
        var bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_marker_building)
        buildingBitmap = Bitmap.createScaledBitmap(bitmap, (32).toPx, (32).toPx, false)

        bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_custom_place)
        placeBitmap = Bitmap.createScaledBitmap(bitmap, (40).toPx, (59).toPx, false)

        bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_marker_food)
        foodBitmap = Bitmap.createScaledBitmap(bitmap, (56).toPx, (70).toPx, false)

        bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_marker_beer)
        beerBitmap = Bitmap.createScaledBitmap(bitmap, (56).toPx, (70).toPx, false)

        bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_marker_electricity)
        electricityBitmap = Bitmap.createScaledBitmap(bitmap, (56).toPx, (70).toPx, false)
    }

    private fun fillUI(buildings: List<BuildingData>) {
        currentMarkers.forEach { it.remove() }

        buildings.forEach {
            val building = it.building
            val lessons = it.lessons
            var text = "${building.title}\n"
            lessons.forEachIndexed { index, lesson,  ->
                val start = lesson.start.zonedDateTime().localHM()
                val end = lesson.end.zonedDateTime().localHM()
                text += "Пара о $start - $end"
                if(index != lessons.lastIndex) text += "\n\n"
            }

            val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.map_marker_building)
            val resized = Bitmap.createScaledBitmap(bitmap, (32).toPx, (32).toPx, false)
            val markerOptions = MarkerOptions()
                .position(it.building.location)
                .title(text)
                .icon(BitmapDescriptorFactory.fromBitmap(resized))

            val m = map.addMarker(
                markerOptions
            )
            if (m != null) {
                currentMarkers.add(m)
            }
        }
    }

    private fun drawNewMarker() {
        newMarker = map.addMarker(
            MarkerOptions()
                .position(map.cameraPosition.target)
                .icon(BitmapDescriptorFactory.fromBitmap(placeBitmap))
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

        viewModel.setDate(today = false)

        lifecycleScope.launch {
            viewModel.buildingsFlow.collectLatest {
                fillUI(it)
            }
        }

        val latLng = LatLng(46.460054, 30.751629)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))


        val adapter = CustomInfoWindowAdapter(requireContext())
        map.setInfoWindowAdapter(adapter)
    }

    override fun onIconChanged(drawable: Int) {
        val bitmap = when(drawable){
            R.drawable.map_marker_food -> foodBitmap
            R.drawable.map_marker_beer -> beerBitmap
            else -> electricityBitmap
        }
        newMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }

    override fun onTextAdded(text: String) {
        newMarker?.title = text
    }
}