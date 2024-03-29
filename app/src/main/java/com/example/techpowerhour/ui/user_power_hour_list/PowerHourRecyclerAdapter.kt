package com.example.techpowerhour.ui.user_power_hour_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.techpowerhour.R
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.util.DateHelper

class PowerHourRecyclerAdapter(
    private val data: List<PowerHour>,
    private val editCallback: (powerHour: PowerHour) -> Unit,
    private val deleteCallback: (powerHour: PowerHour) -> Unit
): RecyclerView.Adapter<PowerHourRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_two_line_two_icon, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        // set the top line of text as the Power Hour name
        holder.textPrimary.text = item.name
        // set the bottom line of text as the date of the Power Hour
        holder.textSecondary.text = DateHelper.displayDate(item.epochDate!!)
        // set the edit button to run the callback function
        holder.editButton.setOnClickListener { editCallback(item) }
        // set the delete button to run the callback function
        holder.deleteButton.setOnClickListener { deleteCallback(item) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPrimary: TextView = view.findViewById(R.id.mtrl_list_item_text)
        val textSecondary: TextView = view.findViewById(R.id.mtrl_list_item_secondary_text)
        val editButton: ImageButton = view.findViewById(R.id.mtrl_list_item_icon_edit)
        val deleteButton: ImageButton = view.findViewById(R.id.mtrl_list_item_icon_delete)
    }
}