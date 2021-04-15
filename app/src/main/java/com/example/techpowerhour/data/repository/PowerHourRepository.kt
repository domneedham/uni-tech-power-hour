package com.example.techpowerhour.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.service.PowerHourService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import kotlin.collections.ArrayList

/**
 * The repository for all things related to the Power Hours.
 * @param service The Power Hour service to fetch data from.
 */
class PowerHourRepository(private val service: PowerHourService) : BaseRepository() {
    /**
     * The current logged in user ID to sent to the service.
     */
    private var loggedInUserId: String? = null

    /**
     * The storage of the user Power Hour list. Can be subscribed to to listen for changes.
     */
    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    /**
     * Inserts a new Power Hour into the persistent storage.
     * @param powerHour The Power Hour the user has just created.
     */
    fun insert(powerHour: PowerHour) {
        return service.insert(loggedInUserId!!, powerHour)
    }

    /**
     * Updates a Power Hour in the persistent storage.
     * @param oldPowerHour The Power Hour state before changes.
     * @param newPowerHour The Power Hour with the updated values.
     */
    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        return service.update(loggedInUserId!!, oldPowerHour, newPowerHour)
    }

    /**
     * Deletes a Power Hour from the persistent storage.
     * @param powerHour The Power Hour to delete.
     */
    fun delete(powerHour: PowerHour) {
        return service.delete(loggedInUserId!!, powerHour)
    }

    /**
     * Start the service listener for Power Hour changes, providing a callback to handle events.
     * Each time the callback function is run, the [userPowerHoursLD] gets updated.
     */
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

    /**
     * Returns a Power Hour from the stored list of user Power Hours.
     */
    fun getUserPowerHourById(id: String) : PowerHour? {
        return userPowerHoursLD.value?.find { ph -> ph.id == id }
    }

    /**
     * Sets the [loggedInUserId] to the current user logged in.
     * Then starts the [getPowerHoursForUser] method to fetch and listen to the Power Hour list for the user.
     * @see BaseRepository.onInit
     */
    override fun onInit() {
        super.onInit()
        // set the loggedInUserId value to the current logged in user id from Firebase
        loggedInUserId = FirebaseAuth.getInstance().uid!!
        // start the service request for loading and listening to the users power hour events
        getPowerHoursForUser()
    }

    /**
     * Resets the [loggedInUserId] to null to resemble no user currently logged in.
     * Then empties the [userPowerHoursLD] to clear any link to the users Power Hours.
     * @see BaseRepository.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        // reset the loggedInUserId
        loggedInUserId = null
        // reset power hour value
        userPowerHoursLD.value = ArrayList()
        service.userPowerHoursDataListener.remove()
    }
}