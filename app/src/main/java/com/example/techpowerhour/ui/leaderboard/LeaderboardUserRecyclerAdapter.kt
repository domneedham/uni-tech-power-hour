package com.example.techpowerhour.ui.leaderboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.techpowerhour.R
import com.example.techpowerhour.data.model.LeaderboardUser

class LeaderboardUserRecyclerAdapter(
        private val data: List<LeaderboardUser>
): RecyclerView.Adapter<LeaderboardUserRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_two_line_leading_icon_trialing_text, parent, false)
        return ViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textPrimary.text = item.name
        holder.textSecondary.text = item.points.toString()
        holder.trailingNumber.text = "#${position + 1}"
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPrimary: TextView = view.findViewById(R.id.mtrl_list_item_text)
        val textSecondary: TextView = view.findViewById(R.id.mtrl_list_item_secondary_text)
        val trailingNumber: TextView = view.findViewById(R.id.mtrl_list_trailing_text)
    }
}