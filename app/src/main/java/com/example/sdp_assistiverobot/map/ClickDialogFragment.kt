package com.example.sdp_assistiverobot.map

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.example.sdp_assistiverobot.R

class ClickDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this.context!!)
            val bundle = arguments

            val mHost = targetFragment as MapFragment

            builder.setMessage("r u sure ${bundle?.getString("command")}?")
                .setTitle("Confirmation")
                .setPositiveButton("CONFIRM",
                    DialogInterface.OnClickListener { _, _ ->
                        mHost.onDialogPositiveClick(this)
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimaryRed))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color20))
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

}
