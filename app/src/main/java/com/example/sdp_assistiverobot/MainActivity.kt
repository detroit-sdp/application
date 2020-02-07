package com.example.sdp_assistiverobot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.calendar.CalendarFragment
import com.example.sdp_assistiverobot.dashboard.DashboardFragment
import com.example.sdp_assistiverobot.map.MapFragment
import com.example.sdp_assistiverobot.patients.PatientsFragment
import com.example.sdp_assistiverobot.userpage.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var selectedId: Int = 0
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialise the view, set navigation item selected listener
        openFragment(DashboardFragment())
        bottom_navigation.selectedItemId = selectedId
        bottom_navigation.setOnNavigationItemSelectedListener {
            selectedId = it.itemId
            when (selectedId) {
                R.id.navigation_dashboard -> {
                    toolbar_title.text = "Dashboard"
                    openFragment(DashboardFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_patients -> {
                    toolbar_title.text = "Patients"
                    openFragment(PatientsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_calendar -> {
                    toolbar_title.text = "Calendar"
                    openFragment(CalendarFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    toolbar_title.text = "Map"
                    openFragment(MapFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_me -> {
                    toolbar_title.text = "Me"
                    openFragment(UserFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

}
