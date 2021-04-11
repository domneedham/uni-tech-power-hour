package com.example.techpowerhour.ui.add_power_hour

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.databinding.FragmentAddPowerHourBinding
import com.example.techpowerhour.util.DateHelper
import com.example.techpowerhour.util.DatePickerHelper
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddPowerHourFragment : Fragment() {
    private var powerHourId: String? = null
    private var oldPowerHour: PowerHour? = null

    private lateinit var viewModel: AddPowerHourViewModel

    private var _binding: FragmentAddPowerHourBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var datePicker: DatePickerHelper

    private lateinit var nameField: EditText
    private lateinit var durationField: EditText
    private lateinit var typeField: EditText
    private lateinit var dateField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPowerHourBinding.inflate(inflater, container, false)

        setupViewModelBinding()
        bindTextFields()

        powerHourId = arguments?.getString("id")
        if (powerHourId != null) {
            // change title to show form is in edit mode
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_edit_power_hour)
            // get the full power pour the user wants to edit
            oldPowerHour = viewModel.getPowerHourById(powerHourId!!)
            // copy the new values into the form fields
            copyValuesFromOldPowerHour()
        }

        setupCalendarBinding()
        setupDropdownMenu()
        setupSaveButtonBinding()

        // hide the keyboard when the user clicks anywhere that is not an input field
        binding.contentWrapper.setOnClickListener {
            it.hideKeyboard()
            nameField.clearFocus()
            durationField.clearFocus()
            typeField.clearFocus()
            dateField.clearFocus()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindTextFields() {
        nameField = binding.workoutNameText
        durationField = binding.durationText
        typeField = binding.typeText
        dateField = binding.datePickerText
    }

    private fun copyValuesFromOldPowerHour() {
        nameField.setText(oldPowerHour?.name, TextView.BufferType.NORMAL)
        durationField.setText(oldPowerHour?.minutes.toString(), TextView.BufferType.NORMAL)
        typeField.setText(oldPowerHour?.type.toString(), TextView.BufferType.NORMAL)
        dateField.setText(
                oldPowerHour?.epochDate?.let { DateHelper.displayDate(it) },
                TextView.BufferType.NORMAL
        )
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = AddPowerHourViewModelFactory(Repositories.powerHour.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddPowerHourViewModel::class.java)
    }

    private fun setupCalendarBinding() {
        datePicker = DatePickerHelper(requireContext())

        dateField.setOnClickListener {
            it.hideKeyboard()
            showDatePickerDialog()
        }
    }

    private fun setupDropdownMenu() {
        val items = PowerHourType.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.typeLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.typeText.setOnClickListener {
            it.hideKeyboard()
        }
    }

    private fun setupSaveButtonBinding() {
        binding.floatingActionButton.setOnClickListener {
            val nameText = nameField.text.toString().trim()
            val durationText = durationField.text.toString().trim()
            val typeText = typeField.text.toString().trim()
            val dateText = dateField.text.toString().trim()

            resetFormErrors()

            val errors = checkForFormErrors(nameText, durationText, typeText, dateText)
            if (errors) return@setOnClickListener

            val powerHour: PowerHour = if (powerHourId != null) {
                viewModel.updatePowerHour(
                    oldPowerHour!!,
                    nameText,
                    durationText,
                    typeText,
                    dateText
                )
            } else {
                viewModel.createNewPourHour(
                    nameText,
                    durationText,
                    typeText,
                    dateText
                )
            }

            Snackbar.make(
                requireContext(),
                requireView(),
                resources.getQuantityString(
                        R.plurals.add_ph_on_save_snackbar,
                        powerHour.points!!,
                        powerHour.points!!
                ),
                Snackbar.LENGTH_SHORT
            ).show()

            resetForm()

            // go back to previous fragment
            // can ignore if fails, leave user to navigate
            findNavController().navigateUp()
        }
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)

        datePicker.setMaxDate(DateHelper.todayInMs)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(day: Int, month: Int, year: Int) {
                val dayStr = if (day < 10) "0${day}" else "$day"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"

                val date = DateHelper.parseDateToLocalDate(dayStr, monthStr, year.toString())
                binding.datePickerText.setText(DateHelper.displayDate(date!!))
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
        val nameError = checkForNameError(nameText)
        binding.workoutNameLayout.error = nameError.text

        val durationError = checkForDurationError(durationText)
        binding.durationLayout.error = durationError.text

        val typeError = checkForTypeError(typeText)
        binding.typeLayout.error = typeError.text

        val dateError = checkForDateError(dateText)
        binding.datePickerLayout.error = dateError.text

        return nameError.error || durationError.error || typeError.error || dateError.error
    }

    private fun checkForNameError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, "The name of the workout is missing")

        return FormError(false, null)
    }

    private fun checkForDurationError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, "The duration of the workout is missing")

        if (text.toDoubleOrNull() == null)
            return FormError(true, "The duration must be a number")

        return FormError(false, null)
    }

    private fun checkForTypeError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, "The type of workout is required")

        return FormError(false, null)
    }

    private fun checkForDateError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, "The date of the workout is missing")

        if (DateHelper.parseDateToEpoch(text)!! > DateHelper.todayEpoch)
            return FormError(true, "The workout can't be in the future")

        return FormError(false, null)
    }

    inner class FormError(val error: Boolean, val text: String?)

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}