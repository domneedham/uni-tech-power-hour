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
import com.example.techpowerhour.ui.login.LoginActivity
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.TEST_MODE
import com.example.techpowerhour.databinding.FragmentProfileBinding
import com.example.techpowerhour.ui.add_power_hour.AddPowerHourFragment
import com.example.techpowerhour.ui.user_power_hour_list.UserPowerHourListFragment

class ProfileFragment : Fragment() {
    private var testMode: Boolean = false

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

        if (arguments != null) {
            testMode = requireArguments().getBoolean(TEST_MODE)
        }

        setupViewModelBinding()

        fabFragmentSwitchBinding()
        powerHourListFragmentSwitchBinding()

        signoutBinding()

        if (!testMode) {
            setName()
            changePowerHourStatisticsText()
        }

        return binding.root
    }

    /**
     * Setup the binding to the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = ProfileViewModelFactory(Repositories.powerHour.value, Repositories.user.value, testMode)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
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
     * Set the click binding on the user Power List field to navigate to the [UserPowerHourListFragment].
     */
    private fun powerHourListFragmentSwitchBinding() {
        binding.controlsPowerHourListLayout.setOnClickListener {
            findNavController().navigate(R.id.navigation_user_power_hour_list)
        }
    }

    /**
     * Observe the user Power Hour list and update the UI with the values.
     */
    private fun changePowerHourStatisticsText() {
        viewModel.getPowerHours().observe(viewLifecycleOwner, { phList ->
            val points = phList.sumOf { ph -> ph.points!! }
            val total = phList.count()

            updateTotalPointsText(points)
            updateTotalPowerHoursCompletedText(total)
        })
    }

    /**
     * Update the total points text on the UI with the passed in [points].
     * @param points The total of points the user has earned.
     */
    fun updateTotalPointsText(points: Int) {
        val totalPointsText = resources.getQuantityString(
                R.plurals.profile_statistics_total_points,
                points,
                points
        )
        binding.pointsText.text = totalPointsText
    }

    /**
     * Update the total Power Hours completed text on the UI with the passed in [total].
     * @param total The total Power Hours completed by the user.
     */
    fun updateTotalPowerHoursCompletedText(total: Int) {
        val totalPowerHoursText = resources.getQuantityString(
                R.plurals.profile_statistics_total_power_hours,
                total,
                total
        )
        binding.numberWorkoutsText.text = totalPowerHoursText
    }

    /**
     * Set the click binding for the signout field.
     */
    private fun signoutBinding() {
        binding.accountSignoutLayout.setOnClickListener {
            // TODO: Change code to use R.string
            val builder = AlertDialog.Builder(this.context)
            builder.setMessage("Are you sure you want to signout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.signOut()
                    val nextIntent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(nextIntent)
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        }
    }

    /**
     * Observe the username and update the UI.
     */
    private fun setName() {
        viewModel.username.observe(viewLifecycleOwner, { name ->
            updateName(name)
        })
    }

    fun updateName(name: String) {
        binding.profileHeaderName.text = name
    }
}