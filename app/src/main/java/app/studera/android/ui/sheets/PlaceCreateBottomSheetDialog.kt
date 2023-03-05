package app.studera.android.ui.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.studera.android.R
import app.studera.android.databinding.BottomsheetPlaceCreateBinding
import app.studera.android.util.listeners.PlaceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceCreateBottomSheetDialog(private val placeManager: PlaceManager)
    : BottomSheetDialogFragment(R.layout.bottomsheet_place_create) {

    private var _binding: BottomsheetPlaceCreateBinding? = null
    private val binding get() = _binding!!

    private var isFoodChoosed = false
        set(value) {
            field = value
            if(value) {
                binding.checkboxBeer.isChecked = false
                binding.checkboxElectricity.isChecked = false
            }
        }

    private var isBeerChoosed = false
        set(value) {
            field = value
            if(value) {
                binding.checkboxFood.isChecked = false
                binding.checkboxElectricity.isChecked = false
            }
        }

    private var isElectricityChoosed = false
        set(value) {
            field = value
            if (value) {
                binding.checkboxFood.isChecked = false
                binding.checkboxBeer.isChecked = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetPlaceCreateBinding.inflate(layoutInflater)

        binding.checkboxFood.setOnCheckedChangeListener { _, b ->
            isFoodChoosed = b
            placeManager.onIconChanged(R.drawable.map_marker_food)
        }

        binding.checkboxBeer.setOnCheckedChangeListener { _, b ->
            isBeerChoosed = b
            placeManager.onIconChanged(R.drawable.map_marker_beer)
        }

        binding.checkboxElectricity.setOnCheckedChangeListener { _, b ->
            isElectricityChoosed = b
            placeManager.onIconChanged(R.drawable.map_marker_electricity)
        }

        binding.button.setOnClickListener {
            val text = binding.eventTitleEditText.text.toString()
            if(text.length < 4) {
                Toast.makeText(requireContext(), "Заповніть назву!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            placeManager.onTextAdded(text)

            dismiss()
        }
        return binding.root
    }

}