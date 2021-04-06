package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.ViewModel
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.User
import com.example.techpowerhour.data.repository.PowerHourRepository
import com.example.techpowerhour.util.DateHelper

class LeaderboardViewModel(private val repository: PowerHourRepository) : ViewModel() {
    private val userRepository = Repositories.user

    fun getAllPowerHours(): List<PowerHour> {
        return repository.userPowerHoursLD.value!!
    }

    fun getAllUsers() : List<User> {
        return userRepository.getAll()
    }

    fun leaderboardToday(): List<LeaderboardUser> {
        val leaderboard = ArrayList<LeaderboardUser>()
        val powerHours = getAllPowerHours()
        val users = userRepository.getAll()

        users.forEach {
            val userPowerHours = powerHours
                .filter { ph -> ph.userId == it.id }
            val points = userPowerHours
                .filter { ph -> ph.epochDate == DateHelper.todayEpoch }
                .sumOf { ph -> ph.points!! }

            leaderboard.add(LeaderboardUser(it.name!!, points))
        }

        return leaderboard
    }
}