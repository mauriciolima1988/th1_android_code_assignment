package com.theoremone.formulamoney.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

interface NetworkStateListener {
    fun onNetworkAvailable()
}

class NetworkChecker(private val networkStateListener: NetworkStateListener) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetworkUtil.isConnected(context)
        if (isConnected) {
            networkStateListener.onNetworkAvailable()
        }
    }
}

@Suppress("DEPRECATION")
object NetworkUtil {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
