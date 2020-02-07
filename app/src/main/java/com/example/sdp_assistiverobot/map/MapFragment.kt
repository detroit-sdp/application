package com.example.sdp_assistiverobot.map


import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private lateinit var manager: WifiP2pManager
    private lateinit var mChannel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter().apply {
        // Indicates a change in the Wi-Fi P2P status.
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // Indicates a change in the list of available peers.
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // Indicates this device's details have changed.
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }
    private var isWifiP2pEnabled: Boolean = false
    private var isWifiP2pConnected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    private var peers = mutableListOf<WifiP2pDevice>()
    private lateinit var peersListAdapter: MyAdapter

    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it of the change.
            peersListAdapter.notifyDataSetChanged()
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }

    }
    val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        // InetAddress from WifiP2pInfo struct.
        val groupOwnerAddress: String = info.groupOwnerAddress.hostAddress
        Log.d(TAG, "connectionListener")
        // After the group negotiation, we can determine the group owner (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Initialise WIFI P2P manager and channel
        manager = activity!!.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = manager.initialize(this.context, activity!!.mainLooper, null).also {
            receiver =
                WiFiDirectBroadcastReceiver(
                    manager,
                    it,
                    this
                )
        }

        discovery.setOnClickListener {
            discoverPeers()
        }

        peersListAdapter = MyAdapter(peers) {
            // TODO: Connect the clicked peer
            connectDevice(it)
        }
        val viewManager = LinearLayoutManager(this.context)
        peers_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = peersListAdapter
        }
    }

    private fun discoverPeers() {
        // TODO: Notify users to turn on location and wifi
        manager.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Start searching peers...")
            }
            override fun onFailure(reason: Int) {
                Log.d(TAG, "discoverPeers onFailure(): $reason")
            }
        })
    }

    private fun connectDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager.connect(mChannel, config, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connecting...")
            }
            override fun onFailure(reason: Int) {
                Log.d(TAG, "Connect failed. Retry. $reason")
                Toast.makeText(
                    activity,
                    "Connect failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        receiver =
            WiFiDirectBroadcastReceiver(
                manager,
                mChannel,
                this
            )
        activity!!.registerReceiver(receiver, intentFilter)
        Log.d(TAG, "registerReceiver")
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(receiver)
        Log.d(TAG, "unregisterReceiver")
    }

    fun setIsWifiP2pEnabled(enable: Boolean) {
        isWifiP2pEnabled = enable
    }

    fun setIsWifiP2pConnected(enable: Boolean) {
        isWifiP2pConnected = enable
    }

    private class MyAdapter constructor(val myDataset: List<WifiP2pDevice>, val clickListener: (WifiP2pDevice) -> Unit) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.line_view)
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.patients_list_row, parent, false)
            // set the view's size, margins, paddings and layout parameters
            // ...
            return MyViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.name.text = myDataset[position].deviceName
            holder.itemView.setOnClickListener {
                clickListener(myDataset[position])
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }
}
