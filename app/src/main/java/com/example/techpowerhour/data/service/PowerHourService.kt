package com.example.techpowerhour.data.service

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.repository.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.repository.enums.DatabaseStatisticsDocumentPaths
import com.example.techpowerhour.data.repository.enums.PowerHourDatabaseDateType
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PowerHourService {
    private val db = Firebase.firestore
    private val powerHoursRef = db.collection(DatabaseCollectionPaths.PowerHour.path)
    private val leaderboardRef = db.collection(DatabaseCollectionPaths.Leaderboard.path)
    private val statisticsRef = db.collection(DatabaseCollectionPaths.Statistics.path)

    lateinit var userPowerHoursDataListener: ListenerRegistration

    val userPowerHoursLD = MutableLiveData<List<PowerHour>>()

    fun insert(id: String, powerHour: PowerHour) {
        powerHoursRef.add(powerHour)

        // insert or update values for the day collections
        val powerHourDayDate = powerHour.epochDate!!
        incrementValuesAll(id, powerHourDayDate, powerHour.points!!, PowerHourDatabaseDateType.Day)

        // insert or update values for the week collections
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        incrementValuesAll(id, powerHourWeekDate, powerHour.points!!, PowerHourDatabaseDateType.Week)

        // insert or update values for the month collections
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        incrementValuesAll(id, powerHourMonthDate, powerHour.points!!, PowerHourDatabaseDateType.Month)
    }

    fun update(id: String, oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        powerHoursRef.document(oldPowerHour.id!!).set(newPowerHour)

        // if the updated power hour has changed date
        // the value needs changing in the leaderboard and statistics collections
        if (oldPowerHour.epochDate != newPowerHour.epochDate) {
            // store the dates from the power hour class
            val oldPowerHourDayDate = oldPowerHour.epochDate!!
            val newPowerHourDayDate = newPowerHour.epochDate!!

            // remove the values from the old date as this is no longer the date of the power hour
            decrementValuesAll(id, oldPowerHourDayDate, oldPowerHour.points!!, PowerHourDatabaseDateType.Day)

            // populate the values for the new date
            incrementValuesAll(id, newPowerHourDayDate, oldPowerHour.points!!, PowerHourDatabaseDateType.Day)

            // the new date may have made it change weeks
            // so need to check if the old date week and new date week are different
            val oldPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourWeekDate != newPowerHourWeekDate) {
                // if they are different, remove the values from the old week collections
                decrementValuesAll(id, oldPowerHourWeekDate, oldPowerHour.points!!, PowerHourDatabaseDateType.Week)

                // then put the new values into the new week of the power hour
                incrementValuesAll(id, newPowerHourWeekDate, newPowerHour.points!!, PowerHourDatabaseDateType.Week)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                // if the date has not changed for the week, the points may have so they need updating if the case
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                incrementValuesPoints(id, newPowerHourWeekDate, difference, PowerHourDatabaseDateType.Week)
            }

            // the new date may have made it change months
            // so need to check if the old date month and new date month are different
            val oldPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(oldPowerHour.epochDate!!)
            val newPowerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(newPowerHour.epochDate!!)
            if (oldPowerHourMonthDate != newPowerHourMonthDate) {
                // if they are different, remove the values from the old month collections
                decrementValuesAll(id, oldPowerHourMonthDate, oldPowerHour.points!!, PowerHourDatabaseDateType.Month)

                // then put the new values into the new month of the power hour
                incrementValuesAll(id, newPowerHourMonthDate, newPowerHour.points!!, PowerHourDatabaseDateType.Month)
            } else if (oldPowerHour.points!! != newPowerHour.points!!) {
                // if the date has not changed for the month, the points may have so they need updating if the case
                val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)
                incrementValuesPoints(id, newPowerHourMonthDate, difference, PowerHourDatabaseDateType.Month)
            }

        } else if (oldPowerHour.points!! != newPowerHour.points!!) {
            // if the date has not changed, the points may have changed and so still need updating
            val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)

            // update the points value for the day collections
            val dayDate = oldPowerHour.epochDate!!
            incrementValuesPoints(id, dayDate, difference, PowerHourDatabaseDateType.Day)

            // update the points value for the week collections
            val weekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(dayDate)
            incrementValuesPoints(id, weekDate, difference, PowerHourDatabaseDateType.Week)

            // update the points value for the month collections
            val monthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(dayDate)
            incrementValuesPoints(id, monthDate, difference, PowerHourDatabaseDateType.Month)
        }
    }

    fun delete(id: String, powerHour: PowerHour) {
        powerHoursRef.document(powerHour.id!!).delete()

        // delete values from the day collections
        val powerHourDayDate = powerHour.epochDate!!
        decrementValuesAll(id, powerHourDayDate, powerHour.points!!, PowerHourDatabaseDateType.Day)

        // delete values from the week collections
        val powerHourWeekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(powerHour.epochDate!!)
        decrementValuesAll(id, powerHourWeekDate, powerHour.points!!, PowerHourDatabaseDateType.Week)

        // delete values from the month collections
        val powerHourMonthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(powerHour.epochDate!!)
        decrementValuesAll(id, powerHourMonthDate, powerHour.points!!, PowerHourDatabaseDateType.Month)
    }

    enum class PointsType {
        Increment,
        Decrement
    }

    /**
     * Increment or update the values for:
     * - points in the leaderboard
     * - points in the statistics
     * - the count of power hours in statistics
     */
    private fun incrementValuesAll(id: String, date: Long, points: Int, type: PowerHourDatabaseDateType) {
        updatePointsInLeaderboard(id, date, points, PointsType.Increment, type)
        updatePointsInStatistics(date, points, PointsType.Increment, type)
        updateCountInStatistics(date, PointsType.Increment, type)
    }

    /**
     * Increment (or effectively delete) the values for:
     * - points in the leaderboard
     * - points in the statistics
     * - the count of power hours in statistics
     */
    private fun decrementValuesAll(id: String, date: Long, points: Int, type: PowerHourDatabaseDateType) {
        updatePointsInLeaderboard(id, date, points, PointsType.Decrement, type)
        updatePointsInStatistics(date, points, PointsType.Decrement, type)
        updateCountInStatistics(date, PointsType.Decrement, type)
    }

    /**
     * Increment or update the values for points only:
     * - points in the leaderboard
     * - points in the statistics
     *
     * To be used when there is no need for modifying the count of power hours.
     */
    private fun incrementValuesPoints(id: String, date: Long, points: Int, type: PowerHourDatabaseDateType) {
        updatePointsInLeaderboard(id, date, points, PointsType.Increment, type)
        updatePointsInStatistics(date, points, PointsType.Increment, type)
    }

    private fun updatePointsInLeaderboard(id: String, powerHourDate: Long, powerHourPoints: Int, pointsType: PointsType, dateType: PowerHourDatabaseDateType) {
        val points = getPositiveOrNegativeValue(powerHourPoints, pointsType)

        leaderboardRef
                .document(dateType.type)
                .collection(powerHourDate.toString())
                .document(id)
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

    fun getPowerHoursForUser(id: String) {
        val query = powerHoursRef.whereEqualTo("userId", id)
        userPowerHoursDataListener = query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(ContentValues.TAG, "listen:error", error)
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