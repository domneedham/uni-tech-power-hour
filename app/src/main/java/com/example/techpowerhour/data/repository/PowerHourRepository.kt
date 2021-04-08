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

        // update for the day
        val powerHourDayDate = powerHour.epochDate!!
        updatePointsInLeaderboard(powerHourDayDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Day)
        updatePointsInStatistics(powerHourDayDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Day)
        updateCountInStatistics(powerHourDayDate, PointsType.Increment, PowerHourDateType.Day)

        // update for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourWeekDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Week)
        updatePointsInStatistics(powerHourWeekDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Week)
        updateCountInStatistics(powerHourWeekDate, PointsType.Increment, PowerHourDateType.Week)

        // update for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourMonthDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Month)
        updatePointsInStatistics(powerHourMonthDate, powerHour.points!!, PointsType.Increment, PowerHourDateType.Month)
        updateCountInStatistics(powerHourMonthDate, PointsType.Increment, PowerHourDateType.Month)
    }

    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        powerHoursRef.document(oldPowerHour.id!!).set(newPowerHour)

        if (oldPowerHour.epochDate != newPowerHour.epochDate) {
            // update for the day value
            val oldPowerHourDayDate = oldPowerHour.epochDate!!
            val newPowerHourDayDate = newPowerHour.epochDate!!

            // remove old values
            updatePointsInLeaderboard(oldPowerHourDayDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Day)
            updatePointsInStatistics(oldPowerHourDayDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Day)
            updateCountInStatistics(oldPowerHourDayDate, PointsType.Decrement, PowerHourDateType.Day)

            // add new values
            updatePointsInLeaderboard(newPowerHourDayDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Day)
            updatePointsInStatistics(newPowerHourDayDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Day)
            updateCountInStatistics(newPowerHourDayDate, PointsType.Increment, PowerHourDateType.Day)

            // update for the week value
            val oldPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourWeekDate != newPowerHourWeekDate) {
                // remove old values
                updatePointsInLeaderboard(oldPowerHourWeekDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Week)
                updatePointsInStatistics(oldPowerHourWeekDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Week)
                updateCountInStatistics(oldPowerHourWeekDate, PointsType.Decrement, PowerHourDateType.Week)

                // add new values
                updatePointsInLeaderboard(newPowerHourWeekDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Week)
                updatePointsInStatistics(newPowerHourWeekDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Week)
                updateCountInStatistics(newPowerHourWeekDate, PointsType.Increment, PowerHourDateType.Week)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                updatePointsInLeaderboard(newPowerHourWeekDate, difference, PointsType.Increment, PowerHourDateType.Week)
                updatePointsInStatistics(newPowerHourWeekDate, difference, PointsType.Increment, PowerHourDateType.Week)
            }

            // update for the month value
            val oldPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourMonthDate != newPowerHourMonthDate) {
                // remove old values
                updatePointsInLeaderboard(oldPowerHourMonthDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Month)
                updatePointsInStatistics(oldPowerHourMonthDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDateType.Month)
                updateCountInStatistics(oldPowerHourMonthDate, PointsType.Decrement, PowerHourDateType.Month)

                // add new values
                updatePointsInLeaderboard(newPowerHourMonthDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Month)
                updatePointsInStatistics(newPowerHourMonthDate, newPowerHour.points!!, PointsType.Increment, PowerHourDateType.Month)
                updateCountInStatistics(newPowerHourMonthDate, PointsType.Increment, PowerHourDateType.Month)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                updatePointsInLeaderboard(newPowerHourMonthDate, difference, PointsType.Increment, PowerHourDateType.Month)
                updatePointsInStatistics(newPowerHourMonthDate, difference, PointsType.Increment, PowerHourDateType.Month)
            }
        } else if (oldPowerHour.points!! != newPowerHour.points!!) {
            val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)

            // update day points
            val dayDate = oldPowerHour.epochDate!!
            updatePointsInLeaderboard(dayDate, difference, PointsType.Increment, PowerHourDateType.Day)
            updatePointsInStatistics(dayDate, difference, PointsType.Increment, PowerHourDateType.Day)

            // update week points
            val weekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(dayDate)
            updatePointsInLeaderboard(weekDate, difference, PointsType.Increment, PowerHourDateType.Week)
            updatePointsInStatistics(weekDate, difference, PointsType.Increment, PowerHourDateType.Week)

            // update month points
            val monthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(dayDate)
            updatePointsInLeaderboard(monthDate, difference, PointsType.Increment, PowerHourDateType.Month)
            updatePointsInStatistics(monthDate, difference, PointsType.Increment, PowerHourDateType.Month)
        }
    }

    fun delete(powerHour: PowerHour) {
        powerHoursRef.document(powerHour.id!!).delete()

        // delete value for the day
        val powerHourDayDate = powerHour.epochDate!!
        updatePointsInLeaderboard(powerHourDayDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Day)
        updatePointsInStatistics(powerHourDayDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Day)
        updateCountInStatistics(powerHourDayDate, PointsType.Decrement, PowerHourDateType.Day)

        // delete value for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourWeekDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Week)
        updatePointsInStatistics(powerHourWeekDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Week)
        updateCountInStatistics(powerHourWeekDate, PointsType.Decrement, PowerHourDateType.Week)

        // delete value for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourMonthDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Month)
        updatePointsInStatistics(powerHourMonthDate, powerHour.points!!, PointsType.Decrement, PowerHourDateType.Month)
        updateCountInStatistics(powerHourMonthDate, PointsType.Decrement, PowerHourDateType.Month)
    }

    enum class PointsType {
        Increment,
        Decrement
    }

    enum class PowerHourDateType(val type: String) {
        Day("days"),
        Week("weeks"),
        Month("months")
    }

    private fun updatePointsInLeaderboard(powerHourDate: Long, powerHourPoints: Int, pointsType: PointsType, dateType: PowerHourDateType) {
        val points = getPositiveOrNegativeValue(powerHourPoints, pointsType)

        leaderboardRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document(loggedInUserId!!)
                .set(
                        mapOf("points" to FieldValue.increment(points)),
                        SetOptions.merge()
                )
    }

    private fun updatePointsInStatistics(powerHourDate: Long, powerHourPoints: Int, pointsType: PointsType, dateType: PowerHourDateType) {
        val points = getPositiveOrNegativeValue(powerHourPoints, pointsType)

        statisticsRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document("points")
                .set(
                        mapOf("total" to FieldValue.increment(points)),
                        SetOptions.merge()
                )
    }

    private fun updateCountInStatistics(powerHourDate: Long, pointsType: PointsType, dateType: PowerHourDateType) {
        val count = getPositiveOrNegativeValue(1, pointsType)

        statisticsRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document("power_hours")
                .set(
                        mapOf("count" to FieldValue.increment(count)),
                        SetOptions.merge()
                )
    }

    private fun getPositiveOrNegativeValue(powerHourPoints: Int, pointsType: PointsType) : Double {
        return if (pointsType == PointsType.Increment) {
            powerHourPoints
        } else {
            -powerHourPoints
        }.toDouble()
    }

    private fun getDifferenceInPoints(oldPoints: Int, newPoints: Int) : Int {
        return if (oldPoints < newPoints) {
            oldPoints - newPoints
        } else {
            newPoints - oldPoints
        }
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
        val todayEpoch = DateHelper.todayEpoch.toString()

        val query = statisticsRef.document("days").collection(todayEpoch).document("points")
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisWeekForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val weekEpoch = DateHelper.startOfWeekEpoch.toString()

        val query = statisticsRef.document("weeks").collection(weekEpoch).document("points")
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "Error: $error")
                }

        return totalPoints
    }

    fun getTotalPointsEarnedThisMonthForCompany(): LiveData<Int> {
        val totalPoints: MutableLiveData<Int> = MutableLiveData(0)
        val monthEpoch = DateHelper.startOfMonthEpoch.toString()

        val query = statisticsRef.document("months").collection(monthEpoch).document("points")
        query.get()
                .addOnSuccessListener { document ->
                    val value = document?.data?.get("total").toString()
                    val total = value.toDoubleOrNull()
                    totalPoints.value = total?.toInt() ?: 0
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