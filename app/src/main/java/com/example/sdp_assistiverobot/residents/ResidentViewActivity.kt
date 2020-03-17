package com.example.sdp_assistiverobot.residents

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_resident_view.*

class ResidentViewActivity : AppCompatActivity() {

    private val TAG = "ResidentViewActivity"
    lateinit var resident: Resident
    lateinit var id: String
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
                    putExtra("id", id)
                    putExtra("resident", resident)
                }, EDIT_PROFILE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * If the info is changed, update the activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
                startActivity(data)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

    private fun setResidentInfo() {
        resident = intent.getSerializableExtra("resident") as Resident
        id = intent.getStringExtra("id")
        name.text = "${resident.first} ${resident.last}"
        location.text = "Location: ${resident.location}"
        priority.text = "Priority: ${resident.priority}"
    }
}
