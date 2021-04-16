package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.auth.FirebaseAuth

class AddPowerHourViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var powerHourId: String? = null
    var oldPowerHour: PowerHour? = null

    var selectedType: PowerHourType? = null

    /**
     * Create a new Power Hour from the users input and call the repository to save into
     * persistent storage.
     * @param name The text for the name of the Power Hour.
     * @param duration The text for the duration of the Power Hour.
     * @param type The type of the Power Hour.
     * @param date The text for the date of the Power Hour.
     */
    fun createNewPourHour(name: String, duration: String, type: PowerHourType, date: String): PowerHour {
        val powerHour = PowerHour(
                name,
                duration.toDouble(),
                type,
                DateHelper.parseDateToEpoch(date),
                auth.uid
        )
        repository.insert(powerHour)
        return powerHour
    }

    /**
     * Modify an existing Power Hour from the users input and call the repository to update in
     * persistent storage.
     * @param oldPowerHour The Power Hour object the user would like to change.
     * @param name The text for the name of the Power Hour.
     * @param duration The text for the duration of the Power Hour.
     * @param type The type of the Power Hour.
     * @param date The text for the date of the Power Hour.
     */
    fun updatePowerHour(oldPowerHour: PowerHour, name: String, duration: String, type: PowerHourType, date: String): PowerHour {
        val newPowerHour = oldPowerHour.copy(
            name = name,
            minutes = duration.toDouble(),
            type = type,
            epochDate = DateHelper.parseDateToEpoch(date)
        )

        repository.update(oldPowerHour, newPowerHour)

        return newPowerHour
    }

    /**
     * Retrieve a Power Hour object by the id.
     * @param id The id of the Power Hour.
     */
    fun getPowerHourById(id: String): PowerHour? {
        return repository.getUserPowerHourById(id)
    }
}