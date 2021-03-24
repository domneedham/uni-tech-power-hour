package com.example.techpowerhour.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHelper {
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    /**
     * Parse an Epoch Date into a UI friendly date
     * @param epochDate The epoch date to parse
     */
    fun displayDate(epochDate: Long): String? {
        return LocalDate.ofEpochDay(epochDate).format(formatter)
    }

    /**
     * Parse a string in format "dd-MM-yyyy" to a LocalDate object
     * @param date The date text
     */
    fun parseDateToLocalDate(date: String): LocalDate? {
        return LocalDate.parse(date, formatter)
    }

    /**
     * Parse a string in format "dd-MM-yyyy" to an epoch date
     * @param date The date text
     */
    fun parseDateToEpoch(date: String) : Long? {
        return parseDateToLocalDate(date)?.toEpochDay()
    }
}