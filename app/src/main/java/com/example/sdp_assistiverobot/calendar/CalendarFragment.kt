package com.example.sdp_assistiverobot.calendar


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.fragment_calendar.*

/**
 *
 */
class CalendarFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        calendarView.setOnDateChangeListener{view, year, month, day ->
            Toast.makeText(this.context, "$day/${month+1}/$year", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showEvents() {

    }

    private fun createEvents(): Boolean {
        Toast.makeText(this.context, "Creating events", Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * Add button
     * Search bar
     * Tasks scheduling - priority
     */

}
