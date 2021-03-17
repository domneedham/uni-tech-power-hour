package com.example.techpowerhour.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.databinding.FragmentHomeBinding

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

        // app stuff
        observePowerHourTable()

        fabFragmentSwitchBinding()

        // point statistics
        changePointsEarnedStatistics()

        // power hour statistics

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fabFragmentSwitchBinding() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_power_hour)
        }
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = HomeViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
    }

    private fun observePowerHourTable() {
        viewModel.getAllPowerHours().observe(viewLifecycleOwner, { powerHours ->
            val adapter = ArrayAdapter((activity?.application!!),
                android.R.layout.simple_list_item_1,
                powerHours)
            binding.powerHourList.adapter = adapter
        })
    }

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
            value -> val pointsWeekText = resources.getQuantityString(
                R.plurals.home_company_points_month,
                value,
                value
        )
            binding.companyPointsLayoutMonthText.text = pointsWeekText
        })
    }
}