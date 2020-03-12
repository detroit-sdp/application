package com.example.sdp_assistiverobot.calendar


import android.content.Intent
import android.database.DatabaseErrorHandler
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.Util
import com.example.sdp_assistiverobot.util.Util.convertDateToLong
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.fragment_calendar.*

class CalendarFragment : Fragment(){

    private val TAG = "CalendarFragment"

    private var selectedDay = Util.getDayOfMonth()
    private var selectedMonth = Util.getMonth()
    private var selectedYear = Util.getYear()
    private var isPause = false

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
        showEvents(convertDateToLong("$selectedYear.$selectedMonth.$selectedDay"))
        calendarView.setOnDateChangeListener{_, year, month, day ->
            selectedDay = day
            selectedMonth = month + 1
            selectedYear = year

            showEvents(convertDateToLong("$selectedYear.$selectedMonth.$selectedDay"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.toolbar_menu_double, menu)
        menu?.getItem(1)?.setIcon(R.drawable.baseline_add_black_24)
        menu?.getItem(1)?.iconTintList = ContextCompat.getColorStateList(this.context!!, R.color.colorPrimary)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_search_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this.context!!, R.color.colorPrimary)
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
        val events = ArrayList<Event>()

        DatabaseManager.eventsRef.whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    events.add(DatabaseManager.newEvent(document))
                }

                if (!isPause) {
                    val viewManager = LinearLayoutManager(this.context)
                    val viewAdapter = MyAdapter(events) {event ->
                        Intent(this.context, EventViewActivity::class.java).also {
                            it.putExtra("resident", event)
                            startActivity(it)
                        }
                    }

                    events_container.apply {
                        setHasFixedSize(true)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    private class MyAdapter constructor(val myDataset: List<Event>, val clickListener: (Event) -> Unit) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.row_title)
            val time: TextView = view.findViewById(R.id.row_time)
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
            holder.time.text = "${myDataset[position].hour}:${myDataset[position].minute}".padStart(5,'0')
            holder.title.text = "Move to ${myDataset[position].location}"
            holder.itemView.setOnClickListener {
                clickListener(myDataset[position])
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

}
