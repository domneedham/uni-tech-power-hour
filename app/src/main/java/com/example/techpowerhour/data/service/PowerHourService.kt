package com.example.techpowerhour.data.service

import android.content.ContentValues
import android.util.Log
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.service.enums.DatabaseCollectionPaths
import com.example.techpowerhour.data.service.enums.DatabaseStatisticsDocumentPaths
import com.example.techpowerhour.data.service.enums.PowerHourDatabaseDateType
import com.example.techpowerhour.util.DateHelper
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.math.absoluteValue

/**
 * The service class for the Power Hour persistent storage in Firestore.
 */
class PowerHourService {
    private val powerHourPath = DatabaseCollectionPaths.PowerHour.path
    private val db = Firebase.firestore

    /**
     * The reference to the power_hours collection.
     */
    private val powerHoursRef = db.collection(powerHourPath)

    /**
     * The reference to the leaderboard collection.
     */
    private val leaderboardRef = db.collection(DatabaseCollectionPaths.Leaderboard.path)

    /**
     * The reference to the statistics collection.
     */
    private val statisticsRef = db.collection(DatabaseCollectionPaths.Statistics.path)

    /**
     * The variable for storing the listener registration. This allows it to be stopped from the repository.
     * @see com.example.techpowerhour.data.repository.PowerHourRepository.onDestroy
     */
    lateinit var userPowerHoursDataListener: ListenerRegistration

    /**
     * Enum used to determine whether changes to a Power Hour points have increased or decreased.
     * The value is used to determine methods to call on the Firestore SDK.
     */
    enum class PointsType {
        Increment,
        Decrement
    }

    /**
     * Store a Power Hour in Firestore. Updates the [powerHoursRef], then updates the points and total
     * for the [statisticsRef] and [leaderboardRef] so the values are aligned.
     * @param id The id of the user who created the Power Hour.
     * @param powerHour The Power Hour object to insert.
     */
    fun insert(id: String, powerHour: PowerHour) {
        powerHoursRef.document(id).collection(powerHourPath).add(powerHour)

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

    /**
     * Update a Power Hour in Firestore. Updates the [powerHoursRef], then updates the points and total
     * for the [statisticsRef] and [leaderboardRef] so the values are aligned.
     *
     * Old values are updated (such as points) if the date has not changed. If the date has changed,
     * values are removed from the old date reference and inserted into the new reference. This occurs
     * for each possibility, but does not strictly do them all straight away. For example, the day
     * reference may have changed, but the week or month has not. In this case, only the day reference
     * is updated in the [statisticsRef] and [leaderboardRef]. This pattern continues for all possible
     * changes, only modifying where a change has definitely occurred.
     *
     * @param id The id of the user who created the Power Hour.
     * @param oldPowerHour The Power Hour object before the updates.
     * @param newPowerHour The Power Hour object that has been updated.
     */
    fun update(id: String, oldPowerHour: PowerHour, newPowerHour: PowerHour) {
        powerHoursRef.document(id).collection(powerHourPath)
                .document(oldPowerHour.id!!).set(newPowerHour)

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
                if (difference < 0) {
                    // decrease the value from the current totals. Use absolute value (convert to positive)
                    // as it is converted negative in the function
                    decrementValuesPoints(id, newPowerHourWeekDate, difference.absoluteValue, PowerHourDatabaseDateType.Week)
                }
                if (difference > 0) {
                    // increment the difference between new and old
                    incrementValuesPoints(id, newPowerHourWeekDate, difference, PowerHourDatabaseDateType.Week)
                }
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
                if (difference < 0) {
                    // decrease the value from the current totals. Use absolute value (convert to positive)
                    // as it is converted negative in the function
                    decrementValuesPoints(id, newPowerHourMonthDate, difference.absoluteValue, PowerHourDatabaseDateType.Month)
                }
                if (difference > 0) {
                    // increment the difference between new and old
                    incrementValuesPoints(id, newPowerHourMonthDate, difference, PowerHourDatabaseDateType.Month)
                }
            }

        } else if (oldPowerHour.points!! != newPowerHour.points!!) {
            // if the date has not changed, the points may have changed and so still need updating
            val difference = getDifferenceInPoints(oldPowerHour.points!!, newPowerHour.points!!)

            // get appropriate dates
            val dayDate = oldPowerHour.epochDate!!
            val weekDate = DateHelper.getStartOfWeekEpochFromDayEpoch(dayDate)
            val monthDate = DateHelper.getStartOfMonthEpochFromDayEpoch(dayDate)

            // if the difference is negative
            if (difference < 0) {
                // decrease the value from the current totals. Use absolute value (convert to positive)
                // as it is converted negative in the function
                decrementValuesPoints(id, dayDate, difference.absoluteValue, PowerHourDatabaseDateType.Day)
                decrementValuesPoints(id, weekDate, difference.absoluteValue, PowerHourDatabaseDateType.Week)
                decrementValuesPoints(id, monthDate, difference.absoluteValue, PowerHourDatabaseDateType.Month)
            }
            // if the difference is positive
            if (difference > 0) {
                // increment the difference between new and old
                incrementValuesPoints(id, dayDate, difference, PowerHourDatabaseDateType.Day)
                incrementValuesPoints(id, weekDate, difference, PowerHourDatabaseDateType.Week)
                incrementValuesPoints(id, monthDate, difference, PowerHourDatabaseDateType.Month)
            }
        }
    }

    /**
     * Delete a Power Hour from Firestore. Updates the [powerHoursRef], then updates (by deleting)
     * the points and total for the [statisticsRef] and [leaderboardRef] so the values are aligned.
     * @param id The id of the user who created the Power Hour.
     * @param powerHour The Power Hour object to delete.
     */
    fun delete(id: String, powerHour: PowerHour) {
        powerHoursRef.document(id).collection(powerHourPath).document(powerHour.id!!).delete()

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

    /**
     * Decrement or update the values for points only:
     * - points in the leaderboard
     * - points in the statistics
     *
     * To be used when there is no need for modifying the count of power hours.
     */
    private fun decrementValuesPoints(id: String, date: Long, points: Int, type: PowerHourDatabaseDateType) {
        updatePointsInLeaderboard(id, date, points, PointsType.Decrement, type)
        updatePointsInStatistics(date, points, PointsType.Decrement, type)
    }

    /**
     * Update the points stored in the [leaderboardRef] for day, week or month depending on the
     * [dateType] passed to the function.
     * @param id The user id.
     * @param powerHourDate The date of the Power Hour. This can be an exact day, a start of week or a start of month.
     * @param powerHourPoints The points for the Power Hour.
     * @param pointsType Whether to increment or decrement points from the current value.
     * @param dateType Whether to update the day, week or month document.
     */
    private fun updatePointsInLeaderboard(
            id: String,
            powerHourDate: Long,
            powerHourPoints: Int,
            pointsType: PointsType,
            dateType: PowerHourDatabaseDateType
    ) {
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

    /**
     * Update the points stored in the [statisticsRef] for day, week or month depending on the
     * [dateType] passed to the function.
     * @param powerHourDate The date of the Power Hour. This can be an exact day, a start of week or a start of month.
     * @param powerHourPoints The points for the Power Hour.
     * @param pointsType Whether to increment or decrement points from the current value.
     * @param dateType Whether to update the day, week or month document.
     */
    private fun updatePointsInStatistics(
            powerHourDate: Long,
            powerHourPoints: Int,
            pointsType: PointsType,
            dateType: PowerHourDatabaseDateType
    ) {
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

    /**
     * Update the total count of Power Hours stored in the [statisticsRef] for day, week or month
     * depending on the [dateType] passed to the function.
     * @param powerHourDate The date of the Power Hour. This can be an exact day, a start of week or a start of month.
     * @param pointsType Whether to increment or decrement points from the current value.
     * @param dateType Whether to update the day, week or month document.
     */
    private fun updateCountInStatistics(
            powerHourDate: Long,
            pointsType: PointsType,
            dateType: PowerHourDatabaseDateType
    ) {
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

    /**
     * Decide whether to use a positive or negative integer. The Firestore SDK only allows
     * an increment method, but passing a negative value will do subtraction rather than addition.
     * Therefore, depending on whether the value has gone up or down determines the need for
     * positive or negative values passed to the Firestore SDK.
     * @param powerHourPoints The points value. Also is used for the count, in which case the value is 1.
     * @param pointsType Whether the value should increment or decrement from the current value.
     */
    private fun getPositiveOrNegativeValue(powerHourPoints: Int, pointsType: PointsType) : Double {
        return if (pointsType == PointsType.Increment) {
            powerHourPoints
        } else {
            -powerHourPoints
        }.toDouble()
    }

    /**
     * A method to determine the difference in points between the old and new.
     * @param oldPoints The points of the non-updated Power Hour.
     * @param newPoints The points of the updated Power Hour.
     */
    private fun getDifferenceInPoints(oldPoints: Int, newPoints: Int) : Int {
        return newPoints - oldPoints
    }

    /**
     * Implement a listener on a query to get the Power Hours for the user.
     * @param id The id of the user.
     * @param onChangeCallback The callback function whenever Firebase is updated or on the
     * initial load of the query.
     */
    fun getPowerHoursForUser(
            id: String,
            onChangeCallback: ((powerHour: PowerHour, docId: String, change: DocumentChange.Type) -> Unit)
    ) {
        val query = powerHoursRef.document(id).collection(powerHourPath)
        userPowerHoursDataListener = query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(ContentValues.TAG, "listen:error", error)
                return@addSnapshotListener
            }

            for (dc in value!!.documentChanges) {
                val powerHour = dc.document.toObject<PowerHour>()
                onChangeCallback(powerHour, dc.document.id, dc.type)
            }
        }
    }
}