package com.example.techpowerhour.ui.leaderboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techpowerhour.R
import com.example.techpowerhour.Repositories
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LeaderboardViewModel

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.leaderboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // add in a menu bar to the title, allowing the user to change the date range they are viewing.
        viewModel.changeDateRange(item.itemId)

        // get the new values for the leaderboard
        viewModel.getLeaderboardValues()
        viewModel.changeTitle()
        return true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        setupViewModelBinding()

        layoutManager = LinearLayoutManager(this.context)
        binding.leaderboardRecyclerView.layoutManager = layoutManager

        observeLeaderboard()
        changeTitle()

        return binding.root
    }

    /**
     * Setup the binding to the viewmodel.
     */
    private fun setupViewModelBinding() {
        val viewModelFactory = LeaderboardViewModelFactory(Repositories.leaderboard.value)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
    }

    /**
     * Observe the leaderboard value from the viewmodel and update the UI.
     */
    private fun observeLeaderboard() {
        viewModel.leaderboard.observe(viewLifecycleOwner, {
            updateDisplay(it)
        })
    }

    /**
     * Update the UI with the list of [LeaderboardUser]. If the list is empty, set appropriate
     * text to inform the user that no values could be found.
     * @param items The list of the users.
     */
    fun updateDisplay(items: List<LeaderboardUser>) {
        val adapter = LeaderboardUserRecyclerAdapter(items)
        binding.leaderboardRecyclerView.adapter = adapter

        // if no items in leaderboard, show a message to the user
        if (items.isEmpty()) {
            binding.leaderboardListEmptyText.visibility = View.VISIBLE
        } else {
            binding.leaderboardListEmptyText.visibility = View.GONE
        }
    }

    /**
     * Change the title of the page to show the current date range the user is viewing.
     */
    private fun changeTitle() {
        viewModel.pageTitle.observe(viewLifecycleOwner, {
            binding.leaderboardDateRangeTitle.text = getString(it)
        })
    }

    enum class DateRanges {
        TODAY,
        WEEK,
        MONTH
    }
}