package com.example.techpowerhour.ui.add_power_hour

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.techpowerhour.R
import com.example.techpowerhour.data.model.enums.PowerHourType


class PowerHourTypeArrayAdapter(
        context: Context,
        resource: Int,
        items: Array<PowerHourType>
) : ArrayAdapter<PowerHourType>(context, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // set the view
        var currentItemView = convertView

        // if the view is null then inflate the custom layout
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        // get the PowerHourType from the position
        val item: PowerHourType? = getItem(position)

        // set the text of the item to show in the UI. Use displayName for translation
        val textView = currentItemView!!.findViewById<TextView>(R.id.list_item_text)
        textView.text = context.getString(item!!.displayName)

        return currentItemView
    }
}