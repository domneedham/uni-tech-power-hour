package com.example.techpowerhour.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class PowerHourRepository {
    private val loggedInUserId = FirebaseAuth.getInstance().uid

    private val db = Firebase.firestore
    private val powerHoursRef = db.collection("power_hours")
    private val leaderboardRef = db.collection("leaderboard")
    private val statisticsRef = db.collection("statistics")

    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    init {
        getPowerHoursForUser()
    }

    fun insert(powerHour: PowerHour) {
        powerHoursRef.add(powerHour)

        // update points for the day
        val powerHourDayDate = powerHour.epochDate!!.toString()
        leaderboardRef.document("days").collection(powerHourDayDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("days").collection(powerHourDayDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("days").collection(powerHourDayDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(1.0)),
                SetOptions.merge()
            )

        // update points for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!).toString()
        leaderboardRef.document("weeks").collection(powerHourWeekDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("weeks").collection(powerHourWeekDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("weeks").collection(powerHourWeekDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(1.0)),
                SetOptions.merge()
            )

        // update points for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!).toString()
        leaderboardRef.document("months").collection(powerHourMonthDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("months").collection(powerHourMonthDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("months").collection(powerHourMonthDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(1.0)),
                SetOptions.merge()
            )
    }

    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        powerHoursRef.document(oldPowerHour.id!!).set(newPowerHour)

        if (oldPowerHour.epochDate != newPowerHour.epochDate) {
            // update for the day value
            val oldPowerHourDayDate = oldPowerHour.epochDate!!.toString()
            val newPowerHourDayDate = newPowerHour.epochDate!!.toString()

            // remove old points from leaderboard
            leaderboardRef.document("days").collection(oldPowerHourDayDate).document(loggedInUserId!!)
                .set(
                    mapOf("points" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                    SetOptions.merge()
                )
            // decrement total points for previous day in statistics
            statisticsRef.document("days").collection(oldPowerHourDayDate).document("points")
                .set(
                    mapOf("total" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                    SetOptions.merge()
                )
            // decrement total count for previous day in statistics
            statisticsRef.document("days").collection(oldPowerHourDayDate).document("power_hours")
                .set(
                    mapOf("count" to FieldValue.increment(-1.0)),
                    SetOptions.merge()
                )

            // add new points to leaderboard
            leaderboardRef.document("days").collection(newPowerHourDayDate).document(loggedInUserId!!)
                .set(
                    mapOf("points" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                    SetOptions.merge()
                )
            statisticsRef.document("days").collection(newPowerHourDayDate).document("points")
                .set(
                    mapOf("total" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                    SetOptions.merge()
                )
            statisticsRef.document("days").collection(newPowerHourDayDate).document("power_hours")
                .set(
                    mapOf("count" to FieldValue.increment(1.0)),
                    SetOptions.merge()
                )


            // update for the week value
            val oldPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(oldPowerHour.epochDate!!).toString()
            val newPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(newPowerHour.epochDate!!).toString()
            if (oldPowerHourWeekDate != newPowerHourWeekDate) {
                // remove old points from leaderboard
                leaderboardRef.document("weeks").collection(oldPowerHourWeekDate).document(loggedInUserId!!)
                    .set(
                        mapOf("points" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                // decrement total points for previous day in statistics
                statisticsRef.document("weeks").collection(oldPowerHourWeekDate).document("points")
                    .set(
                        mapOf("total" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                // decrement total count for previous day in statistics
                statisticsRef.document("weeks").collection(oldPowerHourWeekDate).document("power_hours")
                    .set(
                        mapOf("count" to FieldValue.increment(-1.0)),
                        SetOptions.merge()
                    )

                // add new points to leaderboard
                leaderboardRef.document("weeks").collection(newPowerHourWeekDate).document(loggedInUserId!!)
                    .set(
                        mapOf("points" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                statisticsRef.document("weeks").collection(newPowerHourWeekDate).document("points")
                    .set(
                        mapOf("total" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                statisticsRef.document("weeks").collection(newPowerHourWeekDate).document("power_hours")
                    .set(
                        mapOf("count" to FieldValue.increment(1.0)),
                        SetOptions.merge()
                    )
            }

            // update for the month value
            val oldPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(oldPowerHour.epochDate!!).toString()
            val newPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(newPowerHour.epochDate!!).toString()
            if (oldPowerHourMonthDate != newPowerHourMonthDate) {
                // remove old points from leaderboard
                leaderboardRef.document("months").collection(oldPowerHourMonthDate).document(loggedInUserId!!)
                    .set(
                        mapOf("points" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                // decrement total points for previous day in statistics
                statisticsRef.document("months").collection(oldPowerHourMonthDate).document("points")
                    .set(
                        mapOf("total" to FieldValue.increment(-oldPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                // decrement total count for previous day in statistics
                statisticsRef.document("months").collection(oldPowerHourMonthDate).document("power_hours")
                    .set(
                        mapOf("count" to FieldValue.increment(-1.0)),
                        SetOptions.merge()
                    )

                // add new points to leaderboard
                leaderboardRef.document("months").collection(newPowerHourMonthDate).document(loggedInUserId!!)
                    .set(
                        mapOf("points" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                statisticsRef.document("months").collection(newPowerHourMonthDate).document("points")
                    .set(
                        mapOf("total" to FieldValue.increment(newPowerHour.points!!.toDouble())),
                        SetOptions.merge()
                    )
                statisticsRef.document("months").collection(newPowerHourMonthDate).document("power_hours")
                    .set(
                        mapOf("count" to FieldValue.increment(1.0)),
                        SetOptions.merge()
                    )
            }
        }

    }

    fun delete(powerHour: PowerHour) {
        powerHoursRef.document(powerHour.id!!).delete()

        // delete points for the day
        val powerHourDayDate = powerHour.epochDate!!.toString()
        leaderboardRef.document("days").collection(powerHourDayDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("days").collection(powerHourDayDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("days").collection(powerHourDayDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(-1.0)),
                SetOptions.merge()
            )

        // delete points for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!).toString()
        leaderboardRef.document("weeks").collection(powerHourWeekDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("weeks").collection(powerHourWeekDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("weeks").collection(powerHourWeekDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(-1.0)),
                SetOptions.merge()
            )

        // delete points for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!).toString()
        leaderboardRef.document("months").collection(powerHourMonthDate).document(loggedInUserId!!)
            .set(
                mapOf("points" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("months").collection(powerHourMonthDate).document("points")
            .set(
                mapOf("total" to FieldValue.increment(-powerHour.points!!.toDouble())),
                SetOptions.merge()
            )
        statisticsRef.document("months").collection(powerHourMonthDate).document("power_hours")
            .set(
                mapOf("count" to FieldValue.increment(-1.0)),
                SetOptions.merge()
            )
    }

    private fun getPowerHoursForUser() {
        val query = powerHoursRef.whereEqualTo("userId", loggedInUserId)
        query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "listen:error", error)
                return@addSnapshotListener
            }

            val phList = ArrayList<PowerHour>()
            if (userPowerHoursLD.value != null) {
                phList.addAll(userPowerHoursLD.value!!.asIterable())
            }

            for (dc in value!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        val powerHour = dc.document.toObject<PowerHour>()
                        powerHour.id = dc.document.id
                        phList.add(powerHour)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val powerHour = dc.document.toObject<PowerHour>()
                        powerHour.id = dc.document.id

                        val index = phList.indexOfFirst { ph -> ph.id == dc.document.id }
                        if (index != -1) {
                            phList[index] = powerHour
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        val id = dc.document.id
                        phList.removeIf { ph -> ph.id == id }
                    }
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

    fun getTotalPointsEarnedTodayForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch

        val query = leaderboardRef.document("days").collection(todayEpoch.toString())
        query.get()
                .addOnSuccessListener { documents ->
                    var points = 0.0
                    for (doc in documents) {
                        val value = doc.data["points"].toString()
                        points += value.toDouble()
                    }
                    totalPoints.value = points.toInt()
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch

        val query = leaderboardRef.document("weeks").collection(weekEpoch.toString())
        query.get()
                .addOnSuccessListener { documents ->
                    var points = 0.0
                    for (doc in documents) {
                        val value = doc.data["points"].toString()
                        points += value.toDouble()
                    }
                    totalPoints.value = points.toInt()
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        // get difference between current date and first day of the month
        val monthEpoch = DateHelper.startOfMonthEpoch
        val query = leaderboardRef.document("months").collection(monthEpoch.toString())
        query.get()
                .addOnSuccessListener { documents ->
                    var points = 0.0
                    for (doc in documents) {
                        val value = doc.data["points"].toString()
                        points += value.toDouble()
                    }
                    totalPoints.value = points.toInt()
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPowerHoursCompletedTodayForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = statisticsRef.document("days").collection(todayEpoch).document("power_hours")
        query.get()
            .addOnSuccessListener { document ->
                val value = document?.data?.get("count").toString()
                val count = value.toDoubleOrNull()
                totalPowerHours.value = count?.toInt() ?: 0
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisWeekForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = statisticsRef.document("weeks").collection(weekEpoch).document("power_hours")
        query.get()
            .addOnSuccessListener { document ->
                val value = document?.data?.get("count").toString()
                val count = value.toDoubleOrNull()
                totalPowerHours.value = count?.toInt() ?: 0
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return totalPowerHours
    }

    fun getTotalPowerHoursCompletedThisMonthForCompany(): LiveData<Int> {
        val totalPowerHours: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = statisticsRef.document("months").collection(monthEpoch).document("power_hours")
        query.get()
            .addOnSuccessListener { document ->
                val value = document?.data?.get("count").toString()
                val count = value.toDoubleOrNull()
                totalPowerHours.value = count?.toInt() ?: 0
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error: $error")
            }

        return totalPowerHours
    }

    fun getUserPowerHourById(id: String) : PowerHour? {
        return userPowerHoursLD.value?.find { ph -> ph.id == id }
    }
}