package com.example.techpowerhour.ui.leaderboard


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentLeaderboardBinding
import com.example.techpowerhour.util.DateHelper

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LeaderboardViewModel

    private lateinit var layoutManager: LinearLayoutManager

    private var dateRange = DateRanges.TODAY

    private lateinit var allPowerHours: List<PowerHour>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.leaderboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_date_range_today -> {
                dateRange = DateRanges.TODAY
            }
            R.id.action_date_range_week -> {
                dateRange = DateRanges.WEEK
            }
            R.id.action_date_range_month -> {
                dateRange = DateRanges.MONTH
            }
            else -> super.onOptionsItemSelected(item)
        }

        displayFilteredPowerHours()
        changeTitle()
        return true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        layoutManager = LinearLayoutManager(this.context)
        binding.powerHourList.layoutManager = layoutManager

        changeTitle()
        displayFilteredPowerHours()

        return binding.root
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = LeaderboardViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
    }

    private fun displayFilteredPowerHours() {
        val todayEpoch = DateHelper.todayEpoch
        val weekEpoch = DateHelper.startOfWeekEpoch
        val monthEpoch = DateHelper.startOfMonthEpoch

        val leaderboard: List<LeaderboardUser> = when (dateRange) {
            DateRanges.TODAY -> {
                // if the date is equal today
                viewModel.leaderboardToday()
            }
            DateRanges.WEEK -> {
                // if the date is between start of the week and today
                // no need for the end of the week as can't add workouts beyond the current day
//                allPowerHours.filter { it.epochDate!! in weekEpoch..todayEpoch }
                viewModel.leaderboardToday()
            }
            DateRanges.MONTH -> {
                // if the date is between start of the month and today
                // no need for the end of the month as can't add workouts beyond the current day
//                allPowerHours.filter { it.epochDate!! in monthEpoch..todayEpoch }
                viewModel.leaderboardToday()
            }
        }
        val adapter = LeaderboardUserRecyclerAdapter(
            leaderboard,
        )
        binding.powerHourList.adapter = adapter

        // if no items in leaderboard, show a message to the user
        if (leaderboard.isEmpty()) {
            binding.listEmptyText.visibility = View.VISIBLE
        } else {
            binding.listEmptyText.visibility = View.GONE
        }
    }

    private fun changeTitle() {
        binding.leaderboardDateRangeTitle.text = when (dateRange) {
            DateRanges.TODAY -> {
                getString(R.string.leaderboard_title_today)
            }
            DateRanges.WEEK -> {
                getString(R.string.leaderboard_title_week)
            }
            DateRanges.MONTH -> {
                getString(R.string.leaderboard_title_month)
            }
        }
    }

    enum class DateRanges {
        TODAY,
        WEEK,
        MONTH
    }
}