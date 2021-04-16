package com.example.techpowerhour.ui.leaderboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpowerhour.R
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.repository.LeaderboardRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val repository: LeaderboardRepository) : ViewModel() {
    val leaderboard = MutableLiveData<List<LeaderboardUser>>()
    private var dateRange = LeaderboardFragment.DateRanges.TODAY
    val pageTitle = MutableLiveData<Int>()

    init {
        changeTitle()
        getLeaderboardValues()
    }

    /**
     * Change the date range value depending on the option selected.
     */
    fun changeDateRange(option: Int) {
        when (option) {
            R.id.action_date_range_today -> {
                dateRange = LeaderboardFragment.DateRanges.TODAY
            }
            R.id.action_date_range_week -> {
                dateRange = LeaderboardFragment.DateRanges.WEEK
            }
            R.id.action_date_range_month -> {
                dateRange = LeaderboardFragment.DateRanges.MONTH
            }
        }
    }

    /**
     * Update the resource Int for the page title.
     */
    fun changeTitle() {
        when (dateRange) {
            LeaderboardFragment.DateRanges.TODAY -> {
                pageTitle.value = R.string.leaderboard_title_today
            }
            LeaderboardFragment.DateRanges.WEEK -> {
                pageTitle.value = R.string.leaderboard_title_week
            }
            LeaderboardFragment.DateRanges.MONTH -> {
                pageTitle.value = R.string.leaderboard_title_month
            }
        }
    }

    /**
     * Get the new values for the leaderboard.
     */
    fun getLeaderboardValues() {
        viewModelScope.launch {
            val newList = when (dateRange) {
                LeaderboardFragment.DateRanges.TODAY -> {
                    leaderboardToday()
                }
                LeaderboardFragment.DateRanges.WEEK -> {
                    leaderboardWeek()

                }
                LeaderboardFragment.DateRanges.MONTH -> {
                    leaderboardMonth()
                }
            }
            leaderboard.value = newList
        }
    }

    /**
     * Fetch and return the leaderboard list for the current day.
     */
    private suspend fun leaderboardToday() : List<LeaderboardUser> {
        return repository.getLeaderboardListForToday()
    }

    /**
     * Fetch and return the leaderboard list for the current week.
     */
    private suspend fun leaderboardWeek(): List<LeaderboardUser> {
        return repository.getLeaderboardListForWeek()
    }

    /**
     * Fetch and return the leaderboard list for the current month.
     */
    private suspend fun leaderboardMonth(): List<LeaderboardUser> {
        return repository.getLeaderboardListForMonth()
    }
}