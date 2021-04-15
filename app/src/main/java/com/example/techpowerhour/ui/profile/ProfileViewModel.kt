package com.example.techpowerhour.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository

class ProfileViewModel(private val repository: PowerHourRepository) : ViewModel() {
    /**
     * Fetch the user Power Hour list.
     */
    fun getPowerHours(): LiveData<List<PowerHour>> {
        return repository.userPowerHoursLD
    }

}