package com.example.sdp_assistiverobot.calendar


import android.content.Intent
import android.database.DatabaseErrorHandler
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Util
import com.example.sdp_assistiverobot.util.Util.convertDateToLong
import kotlinx.android.synthetic.main.activity_add_event.*
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

        calendarView.setOnDateChangeListener{_, year, month, day ->
            Toast.makeText(this.context, "$year.${month+1}.$day", Toast.LENGTH_SHORT).show()
            selectedDay = day
            selectedMonth = month + 1
            selectedYear = year

            showEvents(convertDateToLong("$year.${month+1}.$day"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.toolbar_menu_double, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action2 -> {
                activity?.startActivity(Intent(this.context, AddEventActivity::class.java).apply {
                    putExtra("date","$selectedYear.${selectedMonth}.$selectedDay")
                })
                true
            }
            R.id.action1 -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEvents(date: Long) {

    }

    private class MyAdapter constructor(val myDataset: ArrayList<Event>, val clickListener: (Event) -> Unit) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.line_view)
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.residents_list_row, parent, false)
            // set the view's size, margins, paddings and layout parameters
            // ...
            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.name.text
            holder.itemView.setOnClickListener {
                clickListener(myDataset[position])
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

}
