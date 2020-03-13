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

class ConfirmDialogFragment : DialogFragment() {

    private val mNetworkManager = NetworkManager.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this.context!!)
            val bundle = arguments
            val name = bundle?.getString("name") as String
            val location = bundle.getString("location") as String
            val priority = bundle.getString("priority") as String
            val mHost = targetFragment as MapFragment

            builder.setTitle("Moving to $name?")
                .setPositiveButton("CONFIRM",
                    DialogInterface.OnClickListener { _, _ ->
                        mHost.onDialogPositiveClick(location, priority)
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, _ ->
                        mHost.onDialogNegativeClick(this)
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

    interface ConfirmDialogListener {
        fun onDialogPositiveClick(location: String, priority: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

//    internal lateinit var listener: ConfirmDialogListener
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            listener = context as ConfirmDialogListener
//        } catch (e: ClassCastException) {
//            // Drop it silently because it is also called by fragment
//        }
//    }

}
