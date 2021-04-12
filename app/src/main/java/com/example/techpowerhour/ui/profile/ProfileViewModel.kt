package com.example.techpowerhour.ui.profile

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.repository.PowerHourRepository

class ProfileViewModel(private val repository: PowerHourRepository) : ViewModel() {
    /**
     * Fetch the total points earned for the user.
     */
    fun getTotalPointsEarned(): Int {
        return repository.getTotalPointsEarnedForUser()
    }

    /**
     * Fetch the total Power Hours completed by the user.
     */
    fun getTotalPowerHours(): Int {
        return repository.getTotalPowerHoursCompletedForUser()
    }
}