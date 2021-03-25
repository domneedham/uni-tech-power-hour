package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.PowerHourRepository

class LeaderboardViewModel(private val repository: PowerHourRepository) : ViewModel() {
    fun getAllPowerHours(): LiveData<List<PowerHour>> {
        return repository.powerHoursLD
    }
}