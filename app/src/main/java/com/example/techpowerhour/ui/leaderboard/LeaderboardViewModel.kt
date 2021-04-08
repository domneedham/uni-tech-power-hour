package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.util.DateHelper

class LeaderboardViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val userRepository = Repositories.user
    private val leaderboardRepository = Repositories.leaderboard

    fun leaderboardToday(): MutableLiveData<List<LeaderboardUser>> {
        return leaderboardRepository.getLeaderboardListForToday()
    }

    fun leaderboardWeek(): MutableLiveData<List<LeaderboardUser>> {
        return leaderboardRepository.getLeaderboardListForWeek()
    }

    fun leaderboardMonth(): MutableLiveData<List<LeaderboardUser>> {
        return leaderboardRepository.getLeaderboardListForMonth()
    }
}