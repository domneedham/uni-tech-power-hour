package com.example.techpowerhour.ui.leaderboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentLeaderboardBinding

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

        observePowerHourTable()

        return binding.root
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = LeaderboardViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
    }

    private fun displayFilteredPowerHours() {
        val filteredPowerHours: List<PowerHour> = when (dateRange) {
            DateRanges.TODAY -> {
                allPowerHours.filter { it.epochDate!!.toInt() == 18710}
            }
            DateRanges.WEEK -> {
                allPowerHours.filter { it.epochDate!!.toInt() in 18707..18710 }
            }
            DateRanges.MONTH -> {
                allPowerHours.filter { it.epochDate!!.toInt() in 18698..18710}
            }
        }
        val adapter = LeaderboardUserRecyclerAdapter(
                filteredPowerHours,
        )
        binding.powerHourList.adapter = adapter
    }

    private fun observePowerHourTable() {
        viewModel.getAllPowerHours().observe(viewLifecycleOwner, { powerHours ->
            allPowerHours = powerHours
            displayFilteredPowerHours()
        })
    }

    enum class DateRanges {
        TODAY,
        WEEK,
        MONTH
    }
}