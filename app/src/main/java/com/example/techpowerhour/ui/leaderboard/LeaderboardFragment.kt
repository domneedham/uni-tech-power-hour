package com.example.techpowerhour.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.techpowerhour.R

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardViewModel: LeaderboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        leaderboardViewModel =
                ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_leaderboard)
        leaderboardViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}