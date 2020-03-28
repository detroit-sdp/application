package com.example.sdp_assistiverobot.map

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager.getResidents

class ConfirmDialogFragment : DialogFragment() {

    private lateinit var mHost: MapFragment
    private lateinit var id: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this.context!!)
            val bundle = arguments
            id = bundle?.getString("id") as String
            val category = bundle.getString("category") as String
            val priority = bundle.getString("priority") as String
            val note = bundle.getString("note") as String
            mHost = targetFragment as MapFragment
            if (id != "Base") {
                val residents = getResidents()
                val resident = residents[id]
                builder.setTitle("Moving to ${resident?.first} ${resident?.last}?")
                    .setMessage("Priority: $priority\nCategory: $category\nNote: $note")
            } else {
                builder.setTitle("Moving to $id?")
                    .setMessage("Priority: $priority\nCategory: $category\nNote: $note")
            }

            builder.setPositiveButton("CONFIRM",
                DialogInterface.OnClickListener { _, _ ->
                    mHost.onDialogPositiveClick(id, category, priority, note)
                })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, _ ->
                        mHost.onDialogNegativeClick(id)
                        dialog.dismiss()
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimaryRed))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimaryDark))
    }


    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mHost.onDialogNegativeClick(id)
    }

    interface ConfirmDialogListener {
        fun onDialogPositiveClick(id: String, category: String, priority: String, note: String)
        fun onDialogNegativeClick(id: String)
    }


}
