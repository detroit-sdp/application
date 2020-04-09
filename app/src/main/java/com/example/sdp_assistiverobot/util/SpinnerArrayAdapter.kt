package com.example.sdp_assistiverobot.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerArrayAdapter<String>(context: Context, resource: Int,
                                  objects : List<String>) : ArrayAdapter<String>(context, resource, objects) {
    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = super.getDropDownView(position, convertView, parent!!)
        val tv = view as TextView
        if (position == 0) { // Set the hint text color gray
            tv.setTextColor(Color.GRAY)
        } else {
            tv.setTextColor(Color.BLACK)
        }
        return view
    }
}