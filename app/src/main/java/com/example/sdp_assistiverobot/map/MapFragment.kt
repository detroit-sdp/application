package com.example.sdp_assistiverobot.map


import android.content.BroadcastReceiver
import android.content.Context
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.WiFiDirectBroadcastReceiver

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        WifiP2pManager.PeerListListener { peers: WifiP2pDeviceList? ->
            Log.d(TAG, "Get peers list.")
        }
    }
}
