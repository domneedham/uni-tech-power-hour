package com.example.techpowerhour.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHelper {
    val todayEpoch = LocalDate.now().toEpochDay()

    private val differenceInDaysWeek = LocalDate.now().dayOfWeek.compareTo(DayOfWeek.MONDAY).toLong()
    val startOfWeekEpoch = LocalDate.now().minusDays(differenceInDaysWeek).toEpochDay()

    private val differenceInDaysStartOfMonth = LocalDate.now().dayOfMonth - 1.toLong()
    val startOfMonthEpoch = LocalDate.now().minusDays(differenceInDaysStartOfMonth).toEpochDay()

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