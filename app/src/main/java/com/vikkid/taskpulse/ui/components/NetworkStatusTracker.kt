package com.vikkid.taskpulse.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NetworkStatusTracker(
    modifier: Modifier = Modifier,
    onStatusChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(true) }

    // 1. DISPOSABLE EFFECT: Lifecycle-bound system callback register & cleanup
    DisposableEffect(Unit) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isOnline = true
                onStatusChange(true)
            }

            override fun onLost(network: Network) {
                isOnline = false
                onStatusChange(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Composable hat-te hi callback unregister hoga (Zero Memory Leak)
        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    if (!isOnline) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFE53935))
                .padding(vertical = 4.dp, horizontal = 12.dp)
        ) {
            Text(
                text = "Offline Mode - Reconnecting...",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}