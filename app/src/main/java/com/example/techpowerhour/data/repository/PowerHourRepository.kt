package com.example.techpowerhour.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.service.PowerHourService
import com.google.firebase.auth.FirebaseAuth

class PowerHourRepository(private val service: PowerHourService) : BaseRepository() {
    private var loggedInUserId: String? = null
    val userPowerHoursLD = service.userPowerHoursLD

    fun insert(powerHour: PowerHour) {
        return service.insert(loggedInUserId!!, powerHour)
    }

    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        return service.update(loggedInUserId!!, oldPowerHour, newPowerHour)
    }

    fun delete(powerHour: PowerHour) {
        return service.delete(loggedInUserId!!, powerHour)
    }

    fun getTotalPointsEarnedForUser(): Int {
        return service.getTotalPointsEarnedForUser()
    }

    fun getTotalPowerHoursCompletedForUser(): Int {
        return service.getTotalPowerHoursCompletedForUser()
    }

    fun getUserPowerHourById(id: String) : PowerHour? {
        return service.getUserPowerHourById(id)
    }

    private fun getPowerHoursForUser() {
        service.getPowerHoursForUser(loggedInUserId!!)
    }

    override fun onInit() {
        super.onInit()
        loggedInUserId = FirebaseAuth.getInstance().uid!!
        getPowerHoursForUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        loggedInUserId = null
    }
}