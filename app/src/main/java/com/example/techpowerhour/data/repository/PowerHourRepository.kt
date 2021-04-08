package com.example.techpowerhour.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.repository.enums.DatabaseStatisticsDocumentPaths
import com.example.techpowerhour.data.repository.enums.PowerHourDatabaseDateType
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
    private val powerHoursRef = db.collection(DatabaseCollectionPaths.PowerHour.path)
    private val leaderboardRef = db.collection(DatabaseCollectionPaths.Leaderboard.path)
    private val statisticsRef = db.collection(DatabaseCollectionPaths.Statistics.path)

    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    init {
        getPowerHoursForUser()
    }

    fun insert(powerHour: PowerHour) {
        powerHoursRef.add(powerHour)

        // update for the day
        val powerHourDayDate = powerHour.epochDate!!
        updatePointsInLeaderboard(powerHourDayDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Day)
        updatePointsInStatistics(powerHourDayDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Day)
        updateCountInStatistics(powerHourDayDate, PointsType.Increment, PowerHourDatabaseDateType.Day)

        // update for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourWeekDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Week)
        updatePointsInStatistics(powerHourWeekDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Week)
        updateCountInStatistics(powerHourWeekDate, PointsType.Increment, PowerHourDatabaseDateType.Week)

        // update for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourMonthDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Month)
        updatePointsInStatistics(powerHourMonthDate, powerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Month)
        updateCountInStatistics(powerHourMonthDate, PointsType.Increment, PowerHourDatabaseDateType.Month)
    }

    fun update(oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        powerHoursRef.document(oldPowerHour.id!!).set(newPowerHour)

        if (oldPowerHour.epochDate != newPowerHour.epochDate) {
            // update for the day value
            val oldPowerHourDayDate = oldPowerHour.epochDate!!
            val newPowerHourDayDate = newPowerHour.epochDate!!

            // remove old values
            updatePointsInLeaderboard(oldPowerHourDayDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Day)
            updatePointsInStatistics(oldPowerHourDayDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Day)
            updateCountInStatistics(oldPowerHourDayDate, PointsType.Decrement, PowerHourDatabaseDateType.Day)

            // add new values
            updatePointsInLeaderboard(newPowerHourDayDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Day)
            updatePointsInStatistics(newPowerHourDayDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Day)
            updateCountInStatistics(newPowerHourDayDate, PointsType.Increment, PowerHourDatabaseDateType.Day)

            // update for the week value
            val oldPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourWeekDate != newPowerHourWeekDate) {
                // remove old values
                updatePointsInLeaderboard(oldPowerHourWeekDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Week)
                updatePointsInStatistics(oldPowerHourWeekDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Week)
                updateCountInStatistics(oldPowerHourWeekDate, PointsType.Decrement, PowerHourDatabaseDateType.Week)

                // add new values
                updatePointsInLeaderboard(newPowerHourWeekDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Week)
                updatePointsInStatistics(newPowerHourWeekDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Week)
                updateCountInStatistics(newPowerHourWeekDate, PointsType.Increment, PowerHourDatabaseDateType.Week)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                updatePointsInLeaderboard(newPowerHourWeekDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Week)
                updatePointsInStatistics(newPowerHourWeekDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Week)
            }

            // update for the month value
            val oldPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourMonthDate != newPowerHourMonthDate) {
                // remove old values
                updatePointsInLeaderboard(oldPowerHourMonthDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Month)
                updatePointsInStatistics(oldPowerHourMonthDate, oldPowerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Month)
                updateCountInStatistics(oldPowerHourMonthDate, PointsType.Decrement, PowerHourDatabaseDateType.Month)

                // add new values
                updatePointsInLeaderboard(newPowerHourMonthDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Month)
                updatePointsInStatistics(newPowerHourMonthDate, newPowerHour.points!!, PointsType.Increment, PowerHourDatabaseDateType.Month)
                updateCountInStatistics(newPowerHourMonthDate, PointsType.Increment, PowerHourDatabaseDateType.Month)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                updatePointsInLeaderboard(newPowerHourMonthDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Month)
                updatePointsInStatistics(newPowerHourMonthDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Month)
            }
        } else if (oldPowerHour.points!! != newPowerHour.points!!) {
            val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)

            // update day points
            val dayDate = oldPowerHour.epochDate!!
            updatePointsInLeaderboard(dayDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Day)
            updatePointsInStatistics(dayDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Day)

            // update week points
            val weekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(dayDate)
            updatePointsInLeaderboard(weekDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Week)
            updatePointsInStatistics(weekDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Week)

            // update month points
            val monthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(dayDate)
            updatePointsInLeaderboard(monthDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Month)
            updatePointsInStatistics(monthDate, difference, PointsType.Increment, PowerHourDatabaseDateType.Month)
        }
    }

    fun delete(powerHour: PowerHour) {
        powerHoursRef.document(powerHour.id!!).delete()

        // delete value for the day
        val powerHourDayDate = powerHour.epochDate!!
        updatePointsInLeaderboard(powerHourDayDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Day)
        updatePointsInStatistics(powerHourDayDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Day)
        updateCountInStatistics(powerHourDayDate, PointsType.Decrement, PowerHourDatabaseDateType.Day)

        // delete value for the week
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourWeekDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Week)
        updatePointsInStatistics(powerHourWeekDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Week)
        updateCountInStatistics(powerHourWeekDate, PointsType.Decrement, PowerHourDatabaseDateType.Week)

        // delete value for the month
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        updatePointsInLeaderboard(powerHourMonthDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Month)
        updatePointsInStatistics(powerHourMonthDate, powerHour.points!!, PointsType.Decrement, PowerHourDatabaseDateType.Month)
        updateCountInStatistics(powerHourMonthDate, PointsType.Decrement, PowerHourDatabaseDateType.Month)
    }

    enum class PointsType {
        Increment,
        Decrement
    }

    private fun updatePointsInLeaderboard(powerHourDate: Long, powerHourPoints: Int, pointsType: PointsType, dateType: PowerHourDatabaseDateType) {
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

    private fun updatePointsInStatistics(powerHourDate: Long, powerHourPoints: Int, pointsType: PointsType, dateType: PowerHourDatabaseDateType) {
        val points = getPositiveOrNegativeValue(powerHourPoints, pointsType)

        statisticsRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document(DatabaseStatisticsDocumentPaths.Points.path)
                .set(
                        mapOf("total" to FieldValue.increment(points)),
                        SetOptions.merge()
                )
    }

    private fun updateCountInStatistics(powerHourDate: Long, pointsType: PointsType, dateType: PowerHourDatabaseDateType) {
        val count = getPositiveOrNegativeValue(1, pointsType)

        statisticsRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document(DatabaseStatisticsDocumentPaths.PowerHours.path)
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

    fun getUserPowerHourById(id: String) : PowerHour? {
        return userPowerHoursLD.value?.find { ph -> ph.id == id }
    }
}