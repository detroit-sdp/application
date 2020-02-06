package com.example.sdp_assistiverobot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

    // WIFI
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }
    var mChannel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null
    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

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

        // WIFI
        mChannel = manager?.initialize(this, mainLooper, null).also {
            receiver = WiFiDirectBroadcastReceiver(manager!!, it!!, this)
        }
        discoverRobot()
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun discoverRobot() {
        manager?.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "discoverPeers onSuccess()")
            }
            override fun onFailure(reason: Int) {
                Log.d(TAG, "discoverPeers onFailure()")
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        receiver.also {
            registerReceiver(it, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        receiver.also {
            unregisterReceiver(it)
        }
    }

}
