package com.example.techpowerhour.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.databinding.FragmentHomeBinding
import com.example.techpowerhour.ui.add_power_hour.AddPowerHourFragment

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        fabFragmentSwitchBinding()

        // point statistics
        changePointsEarnedStatistics()
        // power hour statistics
        changePowerHoursCompletedStatistics()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Set the click binding on the FAB to navigate to the [AddPowerHourFragment].
     */
    private fun fabFragmentSwitchBinding() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_power_hour)
        }
    }

    /**
     * Bind the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = HomeViewModelFactory(Repositories.statistics.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    /**
     * Get the points earned statistics and update the UI to reflect the values.
     */
    private fun changePointsEarnedStatistics() {
        viewModel.getTotalPointsEarnedTodayForCompany().observe(viewLifecycleOwner, {
            value -> val pointsTodayText = resources.getQuantityString(
                R.plurals.home_company_points_today,
                value,
                value
        )
            binding.companyPointsLayoutTodayText.text = pointsTodayText
        })

        viewModel.getTotalPointsEarnedThisWeekForCompany().observe(viewLifecycleOwner, {
            value -> val pointsWeekText = resources.getQuantityString(
                R.plurals.home_company_points_week,
                value,
                value
        )
            binding.companyPointsLayoutWeekText.text = pointsWeekText
        })

        viewModel.getTotalPointsEarnedThisMonthForCompany().observe(viewLifecycleOwner, {
            value -> val pointsMonthText = resources.getQuantityString(
                R.plurals.home_company_points_month,
                value,
                value
        )
            binding.companyPointsLayoutMonthText.text = pointsMonthText
        })
    }

    /**
     * Get the total Power Hours completed statistics and update the UI to reflect the values.
     */
    private fun changePowerHoursCompletedStatistics() {
        viewModel.getTotalPowerHoursCompletedTodayForCompany().observe(viewLifecycleOwner, {
            value -> val powerHoursTodayText = resources.getQuantityString(
                R.plurals.home_company_power_hour_today,
                value,
                value
        )
            binding.companyPowerHoursLayoutTodayText.text = powerHoursTodayText
        })

        viewModel.getTotalPowerHoursCompletedThisWeekForCompany().observe(viewLifecycleOwner, {
            value -> val powerHoursWeekText = resources.getQuantityString(
                R.plurals.home_company_power_hour_week,
                value,
                value
        )
            binding.companyPowerHoursLayoutWeekText.text = powerHoursWeekText
        })

        viewModel.getTotalPowerHoursCompletedThisMonthForCompany().observe(viewLifecycleOwner, {
            value -> val powerHoursMonthText = resources.getQuantityString(
                R.plurals.home_company_power_hour_month,
                value,
                value
        )
            binding.companyPowerHoursLayoutMonthText.text = powerHoursMonthText
        })
    }
}