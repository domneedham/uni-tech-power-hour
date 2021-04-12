package com.example.techpowerhour.util

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import com.example.techpowerhour.R
import java.util.*

/**
 * Utility class to make dealing with the datepicker dialog easier.
 * @param context The context of the application.
 * @param isSpinnerType Optional differential styling for the datepicker, making it of
 * [R.style.SpinnerDatePickerDialog] if true.
 */
class DatePickerHelper(context: Context, isSpinnerType: Boolean = false) {
    private var dialog: DatePickerDialog
    private var callback: Callback? = null
    private val listener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        callback?.onDateSelected(dayOfMonth, monthOfYear, year)
    }

    init {
        // set the style of the spinner according to the isSpinnerType variable.
        val style = if (isSpinnerType) R.style.SpinnerDatePickerDialog else 0
        val cal = Calendar.getInstance()
        // setup the dialog that will be showed
        dialog = DatePickerDialog(context, style, listener,
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    /**
     * Show the datepicker dialog.
     * @param day The day to start the datepicker on.
     * @param month The month to start the datepicker on.
     * @param year The year to start the datepicker on.
     */
    fun showDialog(day: Int, month: Int, year: Int, callback: Callback?) {
        this.callback = callback
        dialog.datePicker.init(year, month, day, null)
        dialog.show()
    }

    /**
     * Set the minimum date that can be selected from the datepicker. This is the earliest possible
     * date that can be selected.
     * @param minDate The earliest possible date available for selection.
     */
    fun setMinDate(minDate: Long) {
        dialog.datePicker.minDate = minDate
    }

    /**
     * Set the maximum date that can be selected from the datepicker. This is the latest possible
     * date that can be selected.
     * @param maxDate The latest possible date available for selection.
     */
    fun setMaxDate(maxDate: Long) {
        dialog.datePicker.maxDate = maxDate
    }

    /**
     * Interface for implementing the callback function for date selection. Provides the template
     * for the function that is possibly called after date selection.
     */
    interface Callback {
        /**
         * Function to implement for what should happen when a date is selected.
         * @param day The day of the month that has been selected.
         * @param month The month of the year that has been selected.
         * @param year The year that has been selected.
         */
        fun onDateSelected(day: Int, month: Int, year: Int)
    }
}