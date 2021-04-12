package com.example.techpowerhour.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A collection of helper functions for dates.
 */
object DateHelper {
    val todayInMs = Calendar.getInstance().timeInMillis
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
     * Parse an LocalDate into a UI friendly date
     * @param localDate The date object to parse
     */
    fun displayDate(localDate: LocalDate): String? {
        return displayDate(localDate.toEpochDay())
    }

    /**
     * Parse day, month, year into a LocalDate
     * @param day The day of the week
     * @param month The month of the year
     * @param year The year
     */
    fun parseDateToLocalDate(day: Int, month: Int, year: Int): LocalDate? {
        return LocalDate.parse("$day-$month-$year", formatter)
    }

    /**
     * Parse day, month, year into a LocalDate
     * @param day The day of the week
     * @param month The month of the year
     * @param year The year
     */
    fun parseDateToLocalDate(day: String, month: String, year: String): LocalDate? {
        return LocalDate.parse("$day-$month-$year", formatter)
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

    /**
     * Gets the Epoch day value for the start of the week relating to the passed in [day].
     * @param day The exact day Epoch value to get the start of week value for.
     */
    fun getStartOfWeekEpochFromDayEpoch(day: Long) : Long {
        val date = LocalDate.ofEpochDay(day)
        val differenceInDaysWeek = date.dayOfWeek.compareTo(DayOfWeek.MONDAY).toLong()
        return date.minusDays(differenceInDaysWeek).toEpochDay()
    }

    /**
     * Gets the Epoch day value for the start of the month relating to the passed in [day].
     * @param day The exact day Epoch value to get the start of month value for.
     */
    fun getStartOfMonthEpochFromDayEpoch(day: Long) : Long {
        val date = LocalDate.ofEpochDay(day)
        val differenceInDaysStartOfMonth = date.dayOfMonth - 1.toLong()
        return date.minusDays(differenceInDaysStartOfMonth).toEpochDay()
    }
}