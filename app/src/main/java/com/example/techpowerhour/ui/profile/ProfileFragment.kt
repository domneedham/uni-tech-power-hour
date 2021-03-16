package com.example.techpowerhour.ui.profile

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
import com.example.techpowerhour.databinding.FragmentProfileBinding
import com.example.techpowerhour.ui.home.HomeViewModel
import com.example.techpowerhour.ui.home.HomeViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        fabFragmentSwitchBinding()
        powerHourListFragmentSwitchBinding()

        changePowerHourStatisticsText()

        return binding.root
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = ProfileViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        binding.viewModel = viewModel
    }

    private fun fabFragmentSwitchBinding() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_power_hour)
        }
    }

    private fun powerHourListFragmentSwitchBinding() {
        binding.controlsPowerHourListLayout.setOnClickListener {
            findNavController().navigate(R.id.navigation_user_power_hour_list)
        }
    }

    private fun changePowerHourStatisticsText() {
        val totalPoints = viewModel.getTotalPointsEarned()
        val totalPointsText = resources.getQuantityString(
                R.plurals.profile_statistics_total_points,
                totalPoints,
                totalPoints
        )
        binding.pointsText.text = totalPointsText

        val totalPowerHours = viewModel.getTotalPowerHours()
        val totalPowerHoursText = resources.getQuantityString(
                R.plurals.profile_statistics_total_power_hours,
                totalPowerHours,
                totalPowerHours
        )
        binding.numberWorkoutsText.text = totalPowerHoursText
    }
}