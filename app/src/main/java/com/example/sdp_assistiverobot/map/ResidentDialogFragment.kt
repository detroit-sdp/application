package com.example.sdp_assistiverobot.map


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment

import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.residents.ResidentViewActivity
import com.example.sdp_assistiverobot.residents.Resident
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_resident_dialog.*

class ResidentDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mResident: Resident
    private lateinit var mHost: MapFragment
    private var sendClicked: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mResident = arguments?.getSerializable("resident") as Resident
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
        location.text = "Location: ${mResident.location}"
        priority.text = "Priority: ${mResident.priority}"

        cancel.setOnClickListener {
            dismiss()
        }

        full_screen.setOnClickListener {
            startActivity(Intent(this.context, ResidentViewActivity::class.java).apply {
                putExtra("resident", mResident)
            })
            dismiss()
        }

        send_comm.setOnClickListener {
            mHost.showAlertDialog("${mResident.first} ${mResident.last}", mResident.location)
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
