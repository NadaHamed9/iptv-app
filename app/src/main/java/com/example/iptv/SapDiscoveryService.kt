package com.example.iptv

import android.util.Log
import java.net.*
import kotlin.concurrent.thread

class SapDiscoveryService(
    private val onChannelDiscovered: (Channel) -> Unit
) {

    @Volatile
    private var isRunning = false
    private var discoveryThread: Thread? = null
    private val discoveredChannels = HashSet<String>()

    companion object {
        private const val TAG = "SapDiscovery"
        private const val SAP_ADDRESS = "224.2.127.254"
        private const val SAP_PORT = 9875
        private const val NETWORK_INTERFACE = "eth0" // Change if needed
    }

    fun startDiscovery() {
        if (isRunning) {
            Log.w(TAG, "Discovery already running")
            return
        }

        isRunning = true
        discoveredChannels.clear()

        discoveryThread = thread(start = true, name = "SapDiscoveryThread") {
            var socket: MulticastSocket? = null

            try {
                val groupAddress = InetAddress.getByName(SAP_ADDRESS)

                // Try to get network interface
                val networkInterface = try {
                    NetworkInterface.getByName(NETWORK_INTERFACE)
                } catch (e: Exception) {
                    Log.w(TAG, "eth0 not found, using default interface")
                    NetworkInterface.getNetworkInterfaces().asSequence()
                        .firstOrNull { it.isUp && !it.isLoopback }
                }

                if (networkInterface == null) {
                    Log.e(TAG, "No suitable network interface found")
                    return@thread
                }

                socket = MulticastSocket(SAP_PORT).apply {
                    reuseAddress = true
                    this.networkInterface = networkInterface
                    joinGroup(InetSocketAddress(groupAddress, SAP_PORT), networkInterface)
                }

                Log.d(TAG, "SAP Discovery started on ${networkInterface.displayName}")

                val buffer = ByteArray(65535)

                while (isRunning && !Thread.currentThread().isInterrupted) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)

                    val message = String(packet.data, 0, packet.length)
                    parseAndAddChannel(message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Discovery error: ${e.message}", e)
            } finally {
                try {
                    socket?.close()
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing socket", e)
                }
            }
        }
    }

    private fun parseAndAddChannel(sdpMessage: String) {
        try {
            // Extract channel name (s= line)
            val nameRegex = Regex("""(?m)^s=(.+)$""")
            val name = nameRegex.find(sdpMessage)?.groupValues?.get(1)?.trim()

            // Extract IP address (c= line)
            val ipRegex = Regex("""(?m)^c=IN IP4\s+([0-9.]+)""")
            val ipAddress = ipRegex.find(sdpMessage)?.groupValues?.get(1)

            // Extract video port (m= line)
            val portRegex = Regex("""(?m)^m=video\s+(\d+)""")
            val videoPort = portRegex.find(sdpMessage)?.groupValues?.get(1)

            if (name != null && ipAddress != null && videoPort != null) {
                val url = "udp://@$ipAddress:$videoPort"
                val uniqueKey = "$name|$url"

                if (discoveredChannels.add(uniqueKey)) {
                    val channel = Channel(
                        name = name,
                        url = url,
                        streamType = Channel.StreamType.UDP
                    )

                    onChannelDiscovered(channel)
                    Log.d(TAG, "Discovered: $name -> $url")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing SDP: ${e.message}")
        }
    }

    fun stopDiscovery() {
        isRunning = false
        try {
            discoveryThread?.interrupt()
            discoveryThread?.join(1000)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping discovery", e)
        }
    }

    fun isDiscovering() = isRunning
}