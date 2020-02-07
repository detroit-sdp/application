package com.example.sdp_assistiverobot.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.example.sdp_assistiverobot.map.MapFragment

class WiFiDirectBroadcastReceiver(private val manager: WifiP2pManager,
                                  private val channel: WifiP2pManager.Channel,
                                  private val fragment: MapFragment): BroadcastReceiver() {

    private val TAG = "WiFiBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                fragment.setIsWifiP2pEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                Log.d(TAG, "Wifi p2p is enabled: $state")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                manager.requestPeers(channel, fragment.peerListListener)
                Log.d(TAG, "P2P peers changed")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
                manager.let{
                    val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo
                    fragment.setIsWifiP2pConnected(networkInfo?.isConnected == true)
                    if (networkInfo?.isConnected == true) {
                        Log.d(TAG, "Connected to p2p network")
                        manager.requestConnectionInfo(channel, fragment.connectionListener)
                    } else {
                        Log.d(TAG, "Disconnected to p2p network")
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }

    }
}