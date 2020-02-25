package com.example.sdp_assistiverobot.calendar


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Util
import kotlinx.android.synthetic.main.fragment_calendar.*

/**
 *
 */
class CalendarFragment : Fragment(){

    private var selectedDay = Util.getDayOfMonth()
    private var selectedMonth = Util.getMonth()
    private var selectedYear = Util.getYear()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        calendarView.setOnDateChangeListener{view, year, month, day ->
            Toast.makeText(this.context, "$day/${month+1}/$year", Toast.LENGTH_SHORT).show()
            selectedDay = day
            selectedMonth = month + 1
            selectedYear = year
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.calendar_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
