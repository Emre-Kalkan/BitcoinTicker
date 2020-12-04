package com.emrekalkan.bitcointicker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityListener: ConnectivityListener? = null
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (connectivityListener != null)
            connectivityListener?.onNetworkStateChanged(checkNetworkConnection(p0))
    }

    fun checkNetworkConnection(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    interface ConnectivityListener {
        fun onNetworkStateChanged(isConnected: Boolean)
    }
}