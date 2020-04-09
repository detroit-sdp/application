package com.example.sdp_assistiverobot.map

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log


class GuardService : Service() {

    var connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val iGuardServices = IGuardServices.Stub.asInterface(service)
            try {
                Log.i("GuardService", "connected with " + iGuardServices.getServiceName())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            startService(Intent(this@GuardService, NetworkCommService::class.java))
            bindService(Intent(this@GuardService, NetworkCommService::class.java), this, Context.BIND_IMPORTANT)
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return object: IGuardServices.Stub() {
            @Throws (RemoteException::class)
            override fun getServiceName(): String {
                return "GuardService"
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService(Intent(this@GuardService, NetworkCommService::class.java))
        bindService(Intent(this, NetworkCommService::class.java), connection, Context.BIND_IMPORTANT)

        return START_STICKY
    }
}
