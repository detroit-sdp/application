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
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.calendar.CalendarFragment
import com.example.sdp_assistiverobot.dashboard.DashboardFragment
import com.example.sdp_assistiverobot.dashboard.DashboardPrototype1Fragment
import com.example.sdp_assistiverobot.dashboard.DashboardPrototype2Fragment
import com.example.sdp_assistiverobot.dashboard.DashboardPrototype3Fragment
import com.example.sdp_assistiverobot.map.MapFragment
import com.example.sdp_assistiverobot.patients.PatientsFragment
import com.example.sdp_assistiverobot.userpage.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var selectedId: Int = R.id.navigation_dashboard
    private val TAG = "MainActivity"
    private lateinit var db: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        db = DatabaseManager.getInstance()
        db.initializeDB()

        val navIds = arrayListOf<Int>()
        for (i in 0 until bottom_navigation.menu.size()) {
            navIds.add(bottom_navigation.menu[i].itemId)
        }

        // Initialise the view, set navigation item selected listener
        if (savedInstanceState != null) {
            selectedId = savedInstanceState.getInt("selectedId")
        }
        bottom_navigation.selectedItemId = selectedId
        chooseFragment()

        bottom_navigation.setOnNavigationItemSelectedListener {
            selectedId = it.itemId
            if (navIds.contains(selectedId)) {
                chooseFragment()
                return@setOnNavigationItemSelectedListener true
            } else {
                return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun chooseFragment() {
        when (selectedId) {
            R.id.navigation_dashboard -> {
                toolbar_title.text = "Dashboard"
                openFragment(DashboardFragment())
//                openFragment(DashboardPrototype1Fragment())
//                openFragment(DashboardPrototype2Fragment())
//                openFragment(DashboardPrototype3Fragment())
            }
            R.id.navigation_patients -> {
                toolbar_title.text = "Residents"
                openFragment(PatientsFragment())
            }
            R.id.navigation_calendar -> {
                toolbar_title.text = "Calendar"
                openFragment(CalendarFragment())
            }
            R.id.navigation_map -> {
                toolbar_title.text = "Map"
                openFragment(MapFragment())
            }
            R.id.navigation_me -> {
                toolbar_title.text = "Me"
                openFragment(UserFragment())
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("selectedId", selectedId)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.detachListener()
    }
}
