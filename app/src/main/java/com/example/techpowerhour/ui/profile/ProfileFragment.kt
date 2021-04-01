package com.example.techpowerhour.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.LoginActivity
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        fabFragmentSwitchBinding()
        powerHourListFragmentSwitchBinding()

        signoutBinding()

        binding.profileHeaderName.text = auth.currentUser!!.displayName!!
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

    private fun signoutBinding() {
        binding.accountSignoutLayout.setOnClickListener {
            val builder = AlertDialog.Builder(this.context)
            builder.setMessage("Are you sure you want to signout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    val nextIntent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(nextIntent)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        }
    }
}