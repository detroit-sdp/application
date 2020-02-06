package com.example.sdp_assistiverobot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WiFiDirectBroadcastReceiver(private val manager: WifiP2pManager?,
                                  private val channel: WifiP2pManager.Channel?,
                                  private val activity: MainActivity): BroadcastReceiver() {

    private val TAG = "WiFiBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d(TAG, "Wifi p2p is enabled")
                } else {
                    Log.d(TAG, "Wifi p2p is disabled")
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                manager?.requestPeers(channel) {peers: WifiP2pDeviceList? ->
                    // Handle peers list
                    Log.d(TAG, "requestPeers()")
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }

    }
}