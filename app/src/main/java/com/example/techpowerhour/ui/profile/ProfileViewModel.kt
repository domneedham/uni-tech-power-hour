package com.example.techpowerhour.ui.profile

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun getTotalPointsEarned(): Int {
        return repository.getTotalPointsEarnedForUser(auth.uid!!)
    }

    fun getTotalPowerHours(): Int {
        return repository.getTotalPowerHoursCompletedForUser(auth.uid!!)
    }
}