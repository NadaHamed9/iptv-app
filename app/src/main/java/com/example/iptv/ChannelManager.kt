package com.example.iptv

import java.util.Collections

object ChannelManager {
    // This list is shared across the whole app
    val channels: MutableList<Channel> = Collections.synchronizedList(mutableListOf())

    private var sapService: SapDiscoveryService? = null
    private var isStarted = false

    fun startDiscovery() {
        if (isStarted) return // Don't start it twice
        isStarted = true

        sapService = SapDiscoveryService { channel ->
            // Only add if we don't have this URL already
            if (!channels.any { it.url == channel.url }) {
                channels.add(channel)
            }
        }
        sapService?.startDiscovery()
    }

    fun stopDiscovery() {
        sapService?.stopDiscovery()
        isStarted = false
    }
}