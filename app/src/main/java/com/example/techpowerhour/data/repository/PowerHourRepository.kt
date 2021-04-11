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
            // create a new empty list
            val phList = ArrayList<PowerHour>()
            // if the current value of the live data is not empty
            if (userPowerHoursLD.value != null) {
                // add all of the existing data into the new list
                phList.addAll(userPowerHoursLD.value!!.asIterable())
            }

            when (change) {
                // if the document change was an addition
                DocumentChange.Type.ADDED -> {
                    powerHour.id = docId
                    // add the new power hour to the list
                    phList.add(powerHour)
                }
                // if the document change was a modification
                DocumentChange.Type.MODIFIED -> {
                    // get the id of the item
                    powerHour.id = docId
                    // find the index
                    val index = phList.indexOfFirst { ph -> ph.id == docId }
                    // ensure the index was found (-1 = not found)
                    if (index != -1) {
                        // change the item at the index found
                        phList[index] = powerHour
                    }
                }
                // if the document change was a removal
                DocumentChange.Type.REMOVED -> {
                    // remove the item from the list if it is found
                    phList.removeIf { ph -> ph.id == docId }
                }
            }
            // reset the value of the live data list
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
        // set the loggedInUserId value to the current logged in user id from Firebase
        loggedInUserId = FirebaseAuth.getInstance().uid!!
        // start the service request for loading and listening to the users power hour events
        getPowerHoursForUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset the loggedInUserId
        loggedInUserId = null
        // reset power hour value
        userPowerHoursLD.value = ArrayList()
    }
}