package com.example.techpowerhour.ui.add_power_hour

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.databinding.FragmentAddPowerHourBinding
import com.example.techpowerhour.util.DatePickerHelper
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddPowerHourFragment : Fragment() {

    private lateinit var viewModel: AddPowerHourViewModel

    private var _binding: FragmentAddPowerHourBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var datePicker: DatePickerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPowerHourBinding.inflate(inflater, container, false)

        setupViewModelBinding()
        setupCalendarBinding()
        setupDropdownMenu()
        setupSaveButtonBinding()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = AddPowerHourViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddPowerHourViewModel::class.java)
        binding.viewModel = viewModel
    }

    private fun setupCalendarBinding() {
        datePicker = DatePickerHelper(this.requireContext())

        binding.datePickerText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupDropdownMenu() {
        val items = PowerHourType.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.typeLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupSaveButtonBinding() {
        binding.floatingActionButton.setOnClickListener {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val date = LocalDate.parse(binding.datePickerText.text.toString(), formatter)

            viewModel.createNewPourHour(
                binding.workoutNameText.text.toString(),
                binding.durationText.text.toString().toDouble(),
                PowerHourType.valueOf(binding.typeText.text.toString()),
                date
            )

            resetForm()
            Snackbar.make(requireContext(), requireView(), "Power hour created!", Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(day: Int, month: Int, year: Int) {
                val dayStr = if (day < 10) "0${day}" else "$day"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"

                binding.datePickerText.setText("${dayStr}-${monthStr}-${year}")
            }
        })
    }

    private fun resetForm() {
        binding.workoutNameText.text?.clear()
        binding.workoutNameText.clearFocus()
        binding.durationText.text?.clear()
        binding.durationText.clearFocus()
        binding.typeText.text?.clear()
        binding.typeText.clearFocus()
        binding.datePickerText.text?.clear()
        binding.datePickerText.clearFocus()
    }
}