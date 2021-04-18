package com.example.techpowerhour.ui.user_power_hour_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.TEST_MODE
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentUserPowerHourListBinding
import com.example.techpowerhour.ui.add_power_hour.AddPowerHourFragment

class UserPowerHourListFragment : Fragment() {
    private var testMode: Boolean = false

    private var _binding: FragmentUserPowerHourListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: UserPowerHourListViewModel
    
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPowerHourListBinding.inflate(inflater, container, false)

        layoutManager = LinearLayoutManager(this.context)
        binding.userPowerHourListRecyclerView.layoutManager = layoutManager

        setupViewModelBinding()

        if (arguments != null) {
            testMode = requireArguments().getBoolean(TEST_MODE)
        }

        // if in test, do not fetch user Power Hours from Firebase
        if (!testMode) {
            observePowerHourTable()
        }

        fabFragmentSwitchBinding()

        return binding.root
    }

    /**
     * Setup binding for the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = UserPowerHourListViewModelFactory(Repositories.powerHour.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserPowerHourListViewModel::class.java)
    }

    /**
     * Start a listener for the users Power Hours to show in the list.
     */
    private fun observePowerHourTable() {
        viewModel.getAllPowerHours().observe(viewLifecycleOwner, { powerHours ->
            updateDisplay(powerHours)
        })
    }

    /**
     * Sort the Power Hours by descending date order and update the UI.
     * @param powerHours The list of Power Hours.
     */
    fun updateDisplay(powerHours: List<PowerHour>) {
        val sortedPowerHours = powerHours.sortedByDescending { it.epochDate }
        val adapter = PowerHourRecyclerAdapter(
                sortedPowerHours,
                { powerHour -> editPowerHour(powerHour) },
                { powerHour -> deletePowerHour(powerHour) }
        )
        binding.userPowerHourListRecyclerView.adapter = adapter

        // if no items in Power Hour list, show a message to the user
        if (powerHours.isEmpty()) {
            binding.userPowerHourListEmptyText.visibility = View.VISIBLE
        } else {
            binding.userPowerHourListEmptyText.visibility = View.GONE
        }
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
     * To be called when a user clicks the delete button on a Power Hour item. Makes a dialog
     * so that the user can confirm or cancel the action before removing from persistent storage.
     * @param powerHour The Power Hour the user clicked on.
     */
    private fun deletePowerHour(powerHour: PowerHour) {
        // TODO: Change this to use R.string resource
        val builder = AlertDialog.Builder(this.context)
        builder.setMessage("Are you sure you want to delete ${powerHour.name}?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deletePowerHour(powerHour)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    /**
     * To be called when the user clicks the edit button on a Power Hour item. Navigates the user
     * to the [AddPowerHourFragment] passing the id of the Power Hour item they clicked on.
     * @param powerHour The Power Hour the user clicked on.
     */
    private fun editPowerHour(powerHour: PowerHour) {
        val bundle = bundleOf("id" to powerHour.id!!)
        findNavController().navigate(R.id.navigation_add_power_hour, bundle)
    }
}