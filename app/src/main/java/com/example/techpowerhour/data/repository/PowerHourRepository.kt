package com.example.techpowerhour.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.service.PowerHourService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange

class PowerHourRepository(private val service: PowerHourService) : BaseRepository() {
    private var loggedInUserId: String? = null
    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    fun insert(powerHour: PowerHour) {
        return service.insert(loggedInUserId!!, powerHour)
    }

    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        return service.update(loggedInUserId!!, oldPowerHour, newPowerHour)
    }

    fun delete(powerHour: PowerHour) {
        return service.delete(loggedInUserId!!, powerHour)
    }

    private fun getPowerHoursForUser() {
        service.getPowerHoursForUser(loggedInUserId!!) { powerHour, docId, change ->
            val phList = ArrayList<PowerHour>()
            if (userPowerHoursLD.value != null) {
                phList.addAll(userPowerHoursLD.value!!.asIterable())
            }
            when (change) {
                DocumentChange.Type.ADDED -> {
                    powerHour.id = docId
                    phList.add(powerHour)
                }
                DocumentChange.Type.MODIFIED -> {
                    powerHour.id = docId

                    val index = phList.indexOfFirst { ph -> ph.id == docId }
                    if (index != -1) {
                        phList[index] = powerHour
                    }
                }
                DocumentChange.Type.REMOVED -> {
                    phList.removeIf { ph -> ph.id == docId }
                }
            }
            userPowerHoursLD.value = phList
        }
    }

    fun getTotalPointsEarnedForUser(): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = userPowerHoursLD.value
                ?.sumOf { powerHour: PowerHour -> powerHour.points!! }

        return points ?: 0
    }

    fun getTotalPowerHoursCompletedForUser(): Int {
        // no need for live data as no way of possible updates when on user page
        // therefore called every refresh anyway
        val points = userPowerHoursLD.value?.count()

        return points ?: 0
    }

    fun getUserPowerHourById(id: String) : PowerHour? {
        return userPowerHoursLD.value?.find { ph -> ph.id == id }
    }

    override fun onInit() {
        super.onInit()
        loggedInUserId = FirebaseAuth.getInstance().uid!!
        getPowerHoursForUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        loggedInUserId = null
        // reset power hour value
        userPowerHoursLD.value = ArrayList()
    }
}