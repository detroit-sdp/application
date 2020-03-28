package com.example.sdp_assistiverobot.residents


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment

import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.map.MapFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_resident_dialog.*

class ResidentDialogFragment : BottomSheetDialogFragment() {

    private val TAG = "ResidentDialogFragment"

    private lateinit var mResident: Resident
    private lateinit var mId: String
    private lateinit var mHost: MapFragment
    private var sendClicked: Boolean = false
    lateinit var category: String
    lateinit var priority: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mResident = arguments?.getSerializable("resident") as Resident
        mId = arguments?.getString("residentId") as String
        mHost = targetFragment as MapFragment
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resident_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()

        name.text = "${mResident.first} ${mResident.last}"
        location.text = mResident.location

        val priorities = resources.getStringArray(R.array.priorities)
        prioritySpinner.adapter = ArrayAdapter<String>(
            this.context!!,
            android.R.layout.simple_spinner_item,
            priorities.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                priority = parent?.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        prioritySpinner.setSelection(priorities.indexOf(mResident.priority))

        val categories = resources.getStringArray(R.array.categories)
        categorySpinner.adapter = ArrayAdapter(
            this.context!!,
            android.R.layout.simple_spinner_item,
            categories.toList()
        ). apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        categorySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = parent?.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        cancel.setOnClickListener {
            dismiss()
        }

        full_screen.setOnClickListener {
            startActivity(Intent(this.context, ResidentViewActivity::class.java).apply {
                putExtra("id", mId)
                putExtra("resident", mResident)
            })
            dismiss()
        }

        send_comm.setOnClickListener {
            if (category == "Category") {
                return@setOnClickListener
            }
            mHost.showAlertDialog(mId, category, priority, noteText.text.toString())
            sendClicked = true
            dismiss()
        }
    }

    interface ResidentDialogListener {
        fun onCloseClicked(dialog: DialogFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (!sendClicked) {
            mHost.onCloseClicked(this)
        }
        Log.d("Dialog", "onDetach")
    }

}
