package com.example.techpowerhour.ui.add_power_hour

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.databinding.FragmentAddPowerHourBinding
import com.example.techpowerhour.util.DatePickerHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
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
            val nameText = binding.workoutNameText.text.toString().trim()
            val durationText = binding.durationText.text.toString().trim()
            val typeText = binding.typeText.text.toString().trim()
            val dateText = binding.datePickerText.text.toString().trim()

            resetFormErrors()

            val errors = checkForFormErrors(nameText, durationText, typeText, dateText)

            if (!errors) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val date = LocalDate.parse(dateText, formatter)

                val powerHour = viewModel.createNewPourHour(
                        nameText,
                        durationText.toDouble(),
                        PowerHourType.valueOf(typeText),
                        date
                )

                resetForm()
                Snackbar.make(
                        requireContext(),
                        requireView(),
                        "Nice job, you earned ${powerHour.points} points!",
                        Snackbar.LENGTH_SHORT
                ).show()

                // go back to previous fragment
                // can ignore if fails, leave user to navigate
                findNavController().navigateUp()
            }
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
        resetFormErrors()

        binding.workoutNameText.text?.clear()
        binding.workoutNameText.clearFocus()
        binding.durationText.text?.clear()
        binding.durationText.clearFocus()
        binding.typeText.text?.clear()
        binding.typeText.clearFocus()
        binding.datePickerText.text?.clear()
        binding.datePickerText.clearFocus()
    }

    private fun resetFormErrors() {
        binding.workoutNameLayout.error = null
        binding.durationLayout.error = null
        binding.typeLayout.error = null
        binding.datePickerLayout.error = null
    }

    private fun checkForFormErrors(nameText: String, durationText: String, typeText: String, dateText: String): Boolean {
        // check for issues in the form
        var errors = false
        if (checkForNameError(nameText)) {
            binding.workoutNameLayout.error = "A name is missing"
            errors = true
        }

        if (checkForDurationError(durationText)) {
            binding.durationLayout.error = "The workout duration is missing"
            errors = true
        }

        if (checkForTypeError(typeText)) {
            binding.typeLayout.error = "The workout type is missing"
            errors = true
        }

        if (checkForDateError(dateText)) {
            binding.datePickerLayout.error = "The date of the workout is missing"
            errors = true
        }

        return errors
    }

    private fun checkForNameError(text: String): Boolean {
        if (text.isEmpty()) return true
        return false
    }

    private fun checkForDurationError(text: String): Boolean {
        if (text.isEmpty() || text.toDoubleOrNull() == null) {
            return true
        }
        return false
    }

    private fun checkForTypeError(text: String): Boolean {
        if (text.isEmpty()) return true
        return false
    }

    private fun checkForDateError(text: String): Boolean {
        if (text.isEmpty()) return true
        return false
    }
}