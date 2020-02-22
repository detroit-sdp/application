package com.example.sdp_assistiverobot.patients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdp_assistiverobot.Util.DatabaseManager
import kotlinx.android.synthetic.main.fragment_residents.*
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.Util.Resident

class PatientsFragment : Fragment() {

    val TAG = "PatientsFragment"
    private lateinit var mInflater: LayoutInflater
    private val db = DatabaseManager.getInstance()
    private var pauseLoad = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mInflater = inflater
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_residents, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addPatient.setOnClickListener {
            // Start add patient activity
            Intent(this.context, AddResidentActivity::class.java).also {
                startActivity((it))
            }
        }
        loadPatients()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.patients_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadPatients() {
        Log.d(TAG, "loadPatients")
        val db = DatabaseManager.getInstance()
        val viewManager = LinearLayoutManager(this.context)
        val viewAdapter = MyAdapter(db.getResidents()) { resident ->
            Intent(this.context, ResidentViewActivity::class.java).also {
                it.putExtra("resident", resident)
                startActivity(it)
            }
        }
        patientsList.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onPause() {
        pauseLoad = true
        super.onPause()
    }
    }

    private class MyAdapter constructor(val myDataset: ArrayList<Resident>, val clickListener: (Resident) -> Unit) :
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
            holder.name.text = "${myDataset[position].first} ${myDataset[position].last}"
            holder.itemView.setOnClickListener {
                clickListener(myDataset[position])
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

