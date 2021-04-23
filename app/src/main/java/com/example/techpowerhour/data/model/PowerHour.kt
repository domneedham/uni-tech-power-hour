package com.example.techpowerhour.data.model

import com.example.techpowerhour.data.model.enums.PowerHourType
import java.time.LocalDate
import kotlin.math.ceil

/**
 * Hold all information about the Power Hour completed.
 * @property name The user defined name of the Power Hour.
 * @property minutes The duration of the Power Hour in minutes.
 * @property type The type of the Power Hour completed.
 * @property epochDate The date the Power Hour was completed formatted as Long for easy serialisation.
 * @property userId The user id who created the Power Hour.
 * @property difficulty The difficulty of the Power Hour. Determined from the type of Power Hour ([PowerHourType] enum).
 * @property points The points earned for the Power Hour.
 */
data class PowerHour(
    var name: String? = null,
    var minutes: Double? = null,
    var type: PowerHourType? = null,
    var epochDate: Long? = null,
    var id: String? = null,
    var userId: String? = null,
) {
    private val difficulty: Int?
        get(): Int? = type?.difficulty

    val points: Int?
        get(): Int? = calculatePoints()

    /**
     * Calculate the total points earned for the Power Hour.
     *
     * Calculated by the formula:
     * [difficulty] * [minutes] / 10 and rounded up.
     */
    private fun calculatePoints() : Int? {
        return (difficulty?.let { minutes?.times(it) })?.div(10)?.let { ceil(it).toInt() }
    }

    override fun toString(): String {
        return "ID: $id \n" +
                "User ID: $userId \n" +
                "Name: $name \n" +
                "Duration: $minutes minutes \n" +
                "Difficulty: $difficulty \n" +
                "Points: $points \n" +
                "Type: ${type?.name} \n" +
                "Date: ${epochDate?.let { LocalDate.ofEpochDay(it) }}"
    }
}