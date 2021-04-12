package com.example.techpowerhour.ui.leaderboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LeaderboardViewModel

    private lateinit var layoutManager: LinearLayoutManager

    private var dateRange = DateRanges.TODAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.leaderboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // add in a menu bar to the title, allowing the user to change the date range they are viewing.
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

        // get the new values and update the title according to the changed value.
        getLeaderboardValues()
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
        getLeaderboardValues()

        return binding.root
    }

    /**
     * Setup the binding to the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = LeaderboardViewModelFactory(Repositories.leaderboard.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
    }

    /**
     * Get the new values for the leaderboard and update the UI accordingly.
     */
    private fun getLeaderboardValues() {
        viewModel.viewModelScope.launch {
            val leaderboard = when (dateRange) {
                DateRanges.TODAY -> {
                    // if the date is equal today
                    viewModel.leaderboardToday()
                }
                DateRanges.WEEK -> {
                    // if the date is between start of the week and today
                    // no need for the end of the week as can't add workouts beyond the current day
                    viewModel.leaderboardWeek()

                }
                DateRanges.MONTH -> {
                    // if the date is between start of the month and today
                    // no need for the end of the month as can't add workouts beyond the current day
                    viewModel.leaderboardMonth()
                }
            }
            updateDisplay(leaderboard)
        }
    }

    /**
     * Update the UI with the list of [LeaderboardUser]. If the list is empty, set appropriate
     * text to inform the user that no values could be found.
     * @param leaderboard The list of [LeaderboardUser] to use for the [LeaderboardUserRecyclerAdapter].
     */
    private fun updateDisplay(leaderboard: List<LeaderboardUser>) {
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

    /**
     * Change the title of the page to show the current date range the user is viewing.
     */
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