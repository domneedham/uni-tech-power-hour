package com.example.techpowerhour.ui.user_power_hour_list

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentProfileBinding
import com.example.techpowerhour.databinding.FragmentUserPowerHourListBinding
import com.example.techpowerhour.ui.profile.ProfileViewModel
import com.example.techpowerhour.ui.profile.ProfileViewModelFactory

class UserPowerHourListFragment : Fragment() {

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
        binding.powerHourList.layoutManager = layoutManager

        setupViewModelBinding()
        observePowerHourTable()

        return binding.root
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = UserPowerHourListViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserPowerHourListViewModel::class.java)
//        binding.viewModel = viewModel
    }

    private fun observePowerHourTable() {
        viewModel.getAllPowerHours().observe(viewLifecycleOwner, { powerHours ->
            val sortedPowerHours = powerHours.sortedByDescending { it.epochDate }
            val adapter = PowerHourRecyclerAdapter(
                    sortedPowerHours,
                    { powerHour -> editPowerHour(powerHour) },
                    { powerHour -> deletePowerHour(powerHour) }
            )
            binding.powerHourList.adapter = adapter
        })
    }

    private fun deletePowerHour(powerHour: PowerHour) {
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

    private fun editPowerHour(powerHour: PowerHour) {
        val bundle = bundleOf("id" to powerHour.id!!)
        findNavController().navigate(R.id.navigation_add_power_hour, bundle)
    }
}