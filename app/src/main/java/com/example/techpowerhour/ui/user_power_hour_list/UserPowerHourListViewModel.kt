package com.example.techpowerhour.ui.user_power_hour_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository

class UserPowerHourListViewModel(private val repository: PowerHourRepository) : ViewModel() {
    /**
     * Fetch and return a observable list of the users Power Hours.
     */
    fun getAllPowerHours(): LiveData<List<PowerHour>> {
        return repository.userPowerHoursLD
    }

    /**
     * Delete a Power Hour from persistent storage.
     * @param powerHour The Power Hour to delete.
     */
    fun deletePowerHour(powerHour: PowerHour) {
        return repository.delete(powerHour)
    }
}