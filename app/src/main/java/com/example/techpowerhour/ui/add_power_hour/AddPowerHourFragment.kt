package com.example.techpowerhour.ui.add_power_hour

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.techpowerhour.TEST_MODE
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.databinding.FragmentAddPowerHourBinding
import com.example.techpowerhour.util.DateHelper
import com.example.techpowerhour.util.DatePickerHelper
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddPowerHourFragment : Fragment() {
    private var testMode = false

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

        viewModel.powerHourId = arguments?.getString("id")
        if (viewModel.powerHourId != null) {
            // change title to show form is in edit mode
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_edit_power_hour)
            // get the full power pour the user wants to edit
            viewModel.oldPowerHour = viewModel.getPowerHourById(viewModel.powerHourId!!)
            // copy the new values into the form fields
            copyValuesFromOldPowerHour()
        }

        if (arguments != null) {
            val bundleTestMode = requireArguments().get(TEST_MODE)
            val bundleEdit = requireArguments().get("EDIT")
            if (bundleTestMode != null) {
                testMode = bundleTestMode as Boolean
            }
            if (bundleEdit != null && testMode) {
                viewModel.oldPowerHour = PowerHour(
                    "Test",
                    30.0,
                    PowerHourType.Run,
                    DateHelper.todayEpoch,
                    "Test"
                )
                copyValuesFromOldPowerHour()
            }
        }


        setupCalendarBinding()
        setupDropdownMenu()
        setupSaveButtonBinding()

        // hide the keyboard when the user clicks anywhere that is not an input field
        binding.contentWrapper.setOnClickListener {
            it.hideKeyboard()
            binding.workoutNameText.clearFocus()
            binding.durationText.clearFocus()
            binding.typeText.clearFocus()
            binding.datePickerText.clearFocus()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Set the fields to the values of the oldPowerHour if the user is editing.
     */
    private fun copyValuesFromOldPowerHour() {
        binding.workoutNameText.setText(viewModel.oldPowerHour?.name)
        binding.durationText.setText(viewModel.oldPowerHour?.minutes.toString())
        binding.typeText.setText(requireContext().getString(viewModel.oldPowerHour?.type!!.displayName), false)
        binding.datePickerText.setText(viewModel.oldPowerHour?.epochDate?.let { DateHelper.displayDate(it) })

        viewModel.selectedType = viewModel.oldPowerHour?.type
    }

    /**
     * Setup the binding to the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = AddPowerHourViewModelFactory(Repositories.powerHour.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddPowerHourViewModel::class.java)
    }

    /**
     * Setup the binding for the calendar input field click. This method also binds the field
     * click to close the keyboard automatically.
     */
    private fun setupCalendarBinding() {
        datePicker = DatePickerHelper(requireContext())

        binding.datePickerText.setOnClickListener {
            it.hideKeyboard()
            showDatePickerDialog()
        }
    }

    /**
     * Setup the dropdown menu for selecting the Power Hour type. This method also binds the field
     * click to close the keyboard automatically.
     */
    private fun setupDropdownMenu() {
        val items = PowerHourType.values()
        val adapter = PowerHourTypeArrayAdapter(requireContext(), R.layout.list_item, items)

        val textView = (binding.typeLayout.editText as? AutoCompleteTextView)
        textView?.setAdapter(adapter)
        textView?.setOnItemClickListener { parent, _, position, _ ->
            val item = (parent.adapter.getItem(position) as PowerHourType)
            // set the selected type for use in form save
            viewModel.selectedType = item
            // setting filter as false stops issue where the spinner is no longer visible
            textView.setText(context?.getText(item.displayName), false)
        }

        binding.typeText.setOnClickListener {
            it.hideKeyboard()
        }
    }

    /**
     * Setup the binding to the save FAB. Does verification on the data before trying to save
     * to persistent storage through the viewmodel.
     */
    private fun setupSaveButtonBinding() {
        binding.floatingActionButton.setOnClickListener {
            val nameText = binding.workoutNameText.text.toString().trim()
            val durationText = binding.durationText.text.toString().trim()
            val dateText = binding.datePickerText.text.toString().trim()

            resetFormErrors()

            val errors = checkForFormErrors(nameText, durationText, dateText)
            if (errors) return@setOnClickListener

            val powerHour: PowerHour = if (viewModel.powerHourId != null) {
                viewModel.updatePowerHour(
                    viewModel.oldPowerHour!!,
                    nameText,
                    durationText,
                    viewModel.selectedType!!,
                    dateText
                )
            } else {
                viewModel.createNewPourHour(
                    nameText,
                    durationText,
                    viewModel.selectedType!!,
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

    /**
     * Loads the datepicker dialog for the user to select a date.
     * @see DatePickerHelper for how the dialog works.
     */
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

    /**
     * Clears the form errors, inputs and focus to look like first load.
     */
    private fun resetForm() {
        resetFormErrors()

        binding.workoutNameText.text?.clear()
        binding.workoutNameText.clearFocus()

        binding.durationText.text?.clear()
        binding.durationText.clearFocus()

        binding.typeText.text?.clear()
        binding.typeText.clearFocus()
        viewModel.selectedType = null

        binding.datePickerText.text?.clear()
        binding.datePickerText.clearFocus()
    }

    /**
     * Clears the form errors.
     */
    private fun resetFormErrors() {
        binding.workoutNameLayout.error = null
        binding.durationLayout.error = null
        binding.typeLayout.error = null
        binding.datePickerLayout.error = null
    }

    /**
     * Checks for issues with the user input and updates the UI if errors are found.
     * @param nameText The text for the name input.
     * @param durationText The text for the duration input.
     * @param dateText The text for the date input.
     */
    private fun checkForFormErrors(
            nameText: String,
            durationText: String,
            dateText: String
    ): Boolean {
        // check for issues in the form
        val nameError = checkForNameError(nameText)
        binding.workoutNameLayout.error = nameError.text

        val durationError = checkForDurationError(durationText)
        binding.durationLayout.error = durationError.text

        val typeError = checkForTypeError()
        binding.typeLayout.error = typeError.text

        val dateError = checkForDateError(dateText)
        binding.datePickerLayout.error = dateError.text

        return nameError.error || durationError.error || typeError.error || dateError.error
    }

    /**
     * Checks for errors on the nameField
     * @param text The text of the name input.
     */
    private fun checkForNameError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, getString(R.string.add_ph_name_missing))

        return FormError(false, null)
    }

    /**
     * Checks for errors on the durationField.
     * @param text The text of the duration input.
     */
    private fun checkForDurationError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, getString(R.string.add_ph_duration_missing))

        if (text.toDoubleOrNull() == null)
            return FormError(true, getString(R.string.add_ph_duration_not_number))

        return FormError(false, null)
    }

    /**
     * Checks for errors on the typeField.
     */
    private fun checkForTypeError(): FormError {
        if (viewModel.selectedType == null)
            return FormError(true, getString(R.string.add_ph_type_missing))

        return FormError(false, null)
    }

    /**
     * Checks for errors on the dateField.
     * @param text The text of the date input.
     */
    private fun checkForDateError(text: String): FormError {
        if (text.isEmpty())
            return FormError(true, getString(R.string.add_ph_date_missing))

        if (DateHelper.parseDateToEpoch(text)!! > DateHelper.todayEpoch)
            return FormError(true, getString(R.string.add_ph_date_future))

        return FormError(false, null)
    }

    /**
     * Class to represent an error on a form field.
     * @param error Whether there is an error or not.
     * @param text If there is an error, what text should be displayed to the user.
     */
    inner class FormError(val error: Boolean, val text: String?)

    /**
     * Function to hide the keyboard from the user.
     */
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}