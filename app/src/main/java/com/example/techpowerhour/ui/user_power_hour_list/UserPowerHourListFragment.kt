package com.example.techpowerhour.ui.user_power_hour_list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.databinding.FragmentProfileBinding
import com.example.techpowerhour.databinding.FragmentUserPowerHourListBinding
import com.example.techpowerhour.ui.profile.ProfileViewModel
import com.example.techpowerhour.ui.profile.ProfileViewModelFactory

class UserPowerHourListFragment : Fragment() {

    private var _binding: FragmentUserPowerHourListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: UserPowerHourListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserPowerHourListBinding.inflate(inflater, container, false)

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
            val adapter = ArrayAdapter((activity?.application!!),
                    android.R.layout.simple_list_item_1,
                    powerHours)
            binding.powerHourList.adapter = adapter
        })
    }

}