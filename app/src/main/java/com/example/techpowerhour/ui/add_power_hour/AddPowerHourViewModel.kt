package com.example.techpowerhour.ui.add_power_hour

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.auth.FirebaseAuth

class AddPowerHourViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun createNewPourHour(name: String, duration: String, type: String, date: String): PowerHour {
        val powerHour = PowerHour(
                name,
                duration.toDouble(),
                PowerHourType.valueOf(type),
                DateHelper.parseDateToEpoch(date),
                auth.uid
        )
        repository.insert(powerHour)
        return powerHour
    }

    fun updatePowerHour(oldPowerHour: PowerHour, name: String, duration: String, type: String, date: String): PowerHour {
        val newPowerHour = oldPowerHour.copy(
            name = name,
            minutes = duration.toDouble(),
            type = PowerHourType.valueOf(type),
            epochDate = DateHelper.parseDateToEpoch(date)
        )

        repository.update(oldPowerHour, newPowerHour)

        return newPowerHour
    }

    fun getPowerHourById(id: String): PowerHour? {
        return repository.getUserPowerHourById(id)
    }
}