package com.example.sdp_assistiverobot.residents

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.map.ConfirmDialogFragment
import com.example.sdp_assistiverobot.map.NetworkManager
import com.example.sdp_assistiverobot.map.SendCommandRunnable
import com.example.sdp_assistiverobot.util.Resident
import kotlinx.android.synthetic.main.activity_resident_view.*

class ResidentViewActivity : AppCompatActivity(), ConfirmDialogFragment.ConfirmDialogListener {

    private val TAG = "ResidentViewActivity"
    lateinit var resident: Resident
    private val EDIT_PROFILE = 1

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_single, menu)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_edit_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_view)
        setSupportActionBar(findViewById(R.id.resident_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        setResidentInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action0 -> {
                startActivityForResult(Intent(this, EditResidentActivity::class.java).apply {
                    putExtra("resident", resident)
                }, EDIT_PROFILE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
                startActivity(intent)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

    private fun setResidentInfo() {
        resident = intent.getSerializableExtra("resident") as Resident
        name.text = "${resident.first} ${resident.last}"
        location.text = "Location: ${resident.location}"
        priority.text = "Priority: ${resident.priority}"
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.d(TAG, "activity result received")
        setResult(Activity.RESULT_OK, Intent().putExtra("location", resident.location))
        finish()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // Do nothing
    }
}
