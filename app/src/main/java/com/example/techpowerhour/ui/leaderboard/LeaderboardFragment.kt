package com.example.techpowerhour.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LeaderboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        return binding.root
    }

    private fun setupViewModelBinding() {
        val viewModelFactory = LeaderboardViewModelFactory(Repositories.powerHour)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
    }
}