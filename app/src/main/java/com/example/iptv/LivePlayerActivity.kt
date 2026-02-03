//package com.example.iptv
//
//import android.content.Context
//import android.net.Uri
//import android.net.wifi.WifiManager
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.activity.OnBackPressedCallback
//import androidx.annotation.OptIn
//import androidx.core.content.ContextCompat
//import androidx.core.graphics.toColorInt
//import androidx.core.net.toUri
//import androidx.core.view.isVisible
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//
//@OptIn(UnstableApi::class)
//class LivePlayerActivity : BaseActivity() {
//
//    private var player: ExoPlayer? = null
//    private lateinit var playerView: PlayerView
//    private lateinit var sideGuidePanel: View
//    private lateinit var guideOverlay: View
//    private lateinit var guideListView: ListView
//    private lateinit var loadingSpinner: ProgressBar
//    private lateinit var miniInfoBar: View
//    private lateinit var miniChannelNumber: TextView
//    private lateinit var miniChannelName: TextView
//    private lateinit var miniFavoriteIcon: ImageView
//
//    private val channels = mutableListOf<Channel>()
//    private lateinit var adapter: GuideAdapter
//    private var currentIndex = -1
//    private var sapService: SapDiscoveryService? = null
//    private var multicastLock: WifiManager.MulticastLock? = null
//
//    // Register back press callback
//    private val backPressedCallback = object : OnBackPressedCallback(false) {
//        override fun handleOnBackPressed() {
//            // This only runs if enabled (when guide is visible)
//            toggleGuide(false)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_live_player)
//
//        onBackPressedDispatcher.addCallback(this, backPressedCallback)
//
//        playerView = findViewById(R.id.exoPlayerView)
//        sideGuidePanel = findViewById(R.id.sideGuidePanel)
//        guideOverlay = findViewById(R.id.guideOverlay)
//        guideListView = findViewById(R.id.guideListView)
//        loadingSpinner = findViewById(R.id.loadingSpinner)
//        miniInfoBar = findViewById(R.id.miniInfoBar)
//        miniChannelNumber = findViewById(R.id.miniChannelNumber)
//        miniChannelName = findViewById(R.id.miniChannelName)
//        miniFavoriteIcon = findViewById(R.id.miniFavoriteIcon)
//
//        setupPlayer()
//        setupGuide()
//        setupMulticastLock()
//        startChannelDiscovery()
//    }
//
//    private fun setupPlayer() {
//        player = ExoPlayer.Builder(this).build()
//        playerView.player = player
//        player?.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(state: Int) {
//                loadingSpinner.isVisible = state == Player.STATE_BUFFERING
//            }
//        })
//    }
//
//    private fun setupGuide() {
//        adapter = GuideAdapter(this, channels)
//        guideListView.adapter = adapter
//        // Fixed: Removed redundant AdapterView prefix
//        guideListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
//            playChannelAt(pos)
//            toggleGuide(false)
//        }
//    }
//
//    private fun setupMulticastLock() {
//        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        multicastLock = wm.createMulticastLock("iptv_multicast_lock").apply {
//            setReferenceCounted(true)
//            acquire()
//        }
//    }
//
//    private fun startChannelDiscovery() {
//        sapService = SapDiscoveryService { channel ->
//            runOnUiThread {
//                if (channels.none { it.url == channel.url }) {
//                    channels.add(channel)
//                    adapter.notifyDataSetChanged()
//                    if (currentIndex == -1) playChannelAt(0)
//                }
//            }
//        }
//        sapService?.startDiscovery()
//    }
//
//    private fun showMiniInfo() {
//        if (currentIndex == -1) return
//
//        val channel = channels[currentIndex]
//        miniChannelNumber.text = (currentIndex + 1).toString()
//        miniChannelName.text = channel.name
//        miniFavoriteIcon.isVisible = channel.isFavorite
//
//        miniInfoBar.isVisible = true
//        miniInfoBar.removeCallbacks(hideMiniInfoRunnable)
//        miniInfoBar.postDelayed(hideMiniInfoRunnable, 4000)
//    }
//
//    private val hideMiniInfoRunnable = Runnable {
//        miniInfoBar.isVisible = false
//    }
//
//    private fun toggleGuide(show: Boolean) {
//        sideGuidePanel.isVisible = show
//        guideOverlay.isVisible = show
//
//        // Enable back callback ONLY when guide is showing
//        backPressedCallback.isEnabled = show
//
//        if (show) {
//            adapter.notifyDataSetChanged()
//            guideListView.post {
//                guideListView.requestFocus()
//                if (currentIndex != -1) {
//                    guideListView.setSelection(currentIndex)
//                }
//            }
//        }
//    }
//
//    private fun playChannelAt(index: Int) {
//        if (index !in channels.indices) return
//        currentIndex = index
//        val channel = channels[index]
//
//        // Fixed: Used .toUri() extension
//        player?.setMediaItem(MediaItem.fromUri(channel.url.toUri()))
//        player?.prepare()
//        player?.play()
//
//        adapter.notifyDataSetChanged()
//        showMiniInfo()
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        // 1. INTERCEPT BACK BUTTON FIRST
//        // If the guide is open, we manually trigger the back dispatcher
//        if (keyCode == KeyEvent.KEYCODE_BACK && sideGuidePanel.isVisible) {
//            onBackPressedDispatcher.onBackPressed()
//            return true // Tell the system we handled it
//        }
//
//        // 2. HANDLE GUIDE NAVIGATION
//        if (sideGuidePanel.isVisible) {
//            val selected = guideListView.selectedItemPosition
//
//            when (keyCode) {
//                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
//                    if (selected != ListView.INVALID_POSITION) {
//                        playChannelAt(selected)
//                        toggleGuide(false)
//                        return true
//                    }
//                }
//                KeyEvent.KEYCODE_DPAD_DOWN -> {
//                    if (selected == adapter.count - 1) {
//                        guideListView.setSelection(0)
//                        return true
//                    }
//                }
//                KeyEvent.KEYCODE_DPAD_UP -> {
//                    if (selected == 0) {
//                        guideListView.setSelection(adapter.count - 1)
//                        return true
//                    }
//                }
//            }
//
//            // Pass other keys (like DPAD) to the ListView for scrolling
//            return guideListView.onKeyDown(keyCode, event)
//        }
//
//        // 3. FULL SCREEN CONTROLS
//        return when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
//                toggleGuide(true)
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
//                if (channels.isNotEmpty()) {
//                    val next = if (currentIndex > 0) currentIndex - 1 else channels.size - 1
//                    playChannelAt(next)
//                }
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
//                if (channels.isNotEmpty()) {
//                    val next = (currentIndex + 1) % channels.size
//                    playChannelAt(next)
//                }
//                true
//            }
//            else -> super.onKeyDown(keyCode, event)
//        }
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        player?.release()
//        sapService?.stopDiscovery()
//        if (multicastLock?.isHeld == true) multicastLock?.release()
//    }
//
//    private inner class GuideAdapter(context: Context, items: List<Channel>) : ArrayAdapter<Channel>(context, 0, items) {
//        override fun getView(pos: Int, conv: View?, parent: ViewGroup): View {
//            val v = conv ?: View.inflate(context, R.layout.item_channel, null)
//            val item = getItem(pos)!!
//            v.findViewById<TextView>(R.id.channelName).text = item.name
//            val sub = v.findViewById<TextView>(R.id.channelUrl)
//
//            if (pos == currentIndex) {
//                sub.text = "▶ NOW PLAYING"
//                // Fixed: Used .toColorInt() extension
//                sub.setTextColor("#00E5FF".toColorInt())
//                v.isActivated = true
//            } else {
//                // It is better to use String Resources for "CH %d • LIVE" to avoid lint warnings
//                sub.text = "CH ${pos + 1} • LIVE"
//                sub.setTextColor("#88FFFFFF".toColorInt())
//                v.isActivated = false
//            }
//            return v
//        }
//    }
//}
/************* Worked version ***************/
//package com.example.iptv
//
//import android.content.Context
//import android.graphics.Color
//import android.net.Uri
//import android.net.wifi.WifiManager
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.annotation.OptIn
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//
//@OptIn(UnstableApi::class)
//class LivePlayerActivity : BaseActivity() {
//
//    private var player: ExoPlayer? = null
//    private lateinit var playerView: PlayerView
//    private lateinit var sideGuidePanel: View
//    private lateinit var guideOverlay: View
//    private lateinit var guideListView: ListView
//    // private lateinit var loadingSpinner: ProgressBar
//
//    private lateinit var miniInfoBar: View
//    private lateinit var miniChannelNumber: TextView
//    private lateinit var miniChannelName: TextView
//
//    private lateinit var adapter: GuideAdapter
//    private var currentIndex = -1
//    private var multicastLock: WifiManager.MulticastLock? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_live_player)
//
//        // Bind UI
//        playerView = findViewById(R.id.exoPlayerView)
//        sideGuidePanel = findViewById(R.id.sideGuidePanel)
//        guideOverlay = findViewById(R.id.guideOverlay)
//        guideListView = findViewById(R.id.guideListView)
//       // loadingSpinner = findViewById(R.id.loadingSpinner)
//        miniInfoBar = findViewById(R.id.miniInfoBar)
//        miniChannelNumber = findViewById(R.id.miniChannelNumber)
//        miniChannelName = findViewById(R.id.miniChannelName)
//
//        setupPlayer()
//        setupGuide()
//        setupMulticastLock() // Enable multicast to not block  MPEG-TS over UDP live channels
//
//        // If channels were already found by MainActivity, play the first one immediately
//        if (ChannelManager.channels.isNotEmpty() && currentIndex == -1) {
//            playChannelAt(0)
//        }
//    }
//
//    private fun setupPlayer() {
//        player = ExoPlayer.Builder(this).build()
//        playerView.player = player
////        player?.addListener(object : Player.Listener {
////            override fun onPlaybackStateChanged(state: Int) {
////              //  loadingSpinner.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
////            }
////        })
//    }
//
//    private fun setupGuide() {
//        adapter = GuideAdapter(this, ChannelManager.channels)
//        guideListView.adapter = adapter
//
//        // This handles the "OK" button selection directly
//        guideListView.setOnItemClickListener { _, _, position, _ ->
//            playChannelAt(position)
//            toggleGuide(false)
//        }
//    }
//
//    private fun playChannelAt(index: Int) {
//        val list = ChannelManager.channels
//        if (index !in list.indices) return
//
//        currentIndex = index
//        val channel = list[index]
//
//        player?.setMediaItem(MediaItem.fromUri(Uri.parse(channel.url)))
//        player?.prepare()
//        player?.play()
//
//        adapter.notifyDataSetChanged()
//        showMiniInfo()
//    }
//
//    private fun showMiniInfo() {
//        val list = ChannelManager.channels
//        if (currentIndex == -1 || currentIndex >= list.size) return
//
//        val channel = list[currentIndex]
//        miniChannelNumber.text = (currentIndex + 1).toString()
//        miniChannelName.text = channel.name
//
//        miniInfoBar.visibility = View.VISIBLE
//        miniInfoBar.removeCallbacks(hideMiniInfoRunnable)
//        miniInfoBar.postDelayed(hideMiniInfoRunnable, 4000)
//    }
//
//    private val hideMiniInfoRunnable = Runnable { miniInfoBar.visibility = View.GONE }
//
//    private fun toggleGuide(show: Boolean) {
//        if (show) {
//            sideGuidePanel.visibility = View.VISIBLE
//            guideOverlay.visibility = View.VISIBLE
//
//            // REFRESH the list to make sure it's up to date
//            adapter.notifyDataSetChanged()
//
//            // CRITICAL: The list must take focus to respond to the "OK" button
//            guideListView.isFocusable = true
//            guideListView.isFocusableInTouchMode = true
//            guideListView.requestFocus()
//
//            // If we are already watching a channel, highlight it in the list
//            if (currentIndex != -1) {
//                guideListView.setSelection(currentIndex)
//            }
//        } else {
//            sideGuidePanel.visibility = View.GONE
//            guideOverlay.visibility = View.GONE
//        }
//    }
//
//    // Your requested onKeyDown logic with wrap-around
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val listSize = ChannelManager.channels.size
//
//        //Guide list appears
//        if (sideGuidePanel.visibility == View.VISIBLE) {
//            val selected = guideListView.selectedItemPosition
//
//            when (keyCode) {
//                //Pressed OK/Enter
//                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
//                    if (selected != ListView.INVALID_POSITION) {
//                        playChannelAt(selected) // Play the selected channel
//                        toggleGuide(false) // Close the guide
//                        return true
//                    }
//                }
//                //Pressed arrow down
//                KeyEvent.KEYCODE_DPAD_DOWN -> {
//                    // If at last item
//                    if (selected == listSize - 1) {
//                        guideListView.setSelection(0) // Jump to first item
//                        return true
//                    }
//                }
//                //Pressed arrow up
//                KeyEvent.KEYCODE_DPAD_UP -> {
//                    // If at first item
//                    if (selected == 0) {
//                        guideListView.setSelection(listSize - 1) // Jump to last item
//                        return true
//                    }
//                }
//                //Pressed back
//                KeyEvent.KEYCODE_BACK -> {
//                    toggleGuide(false) // Close the guide without changing the channel
//                    return true
//                }
//            }
//            return guideListView.onKeyDown(keyCode, event)
//        }
//
//        //Normal viewing watching a channel with guide closed, handle quick navigation
//        return when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
//                toggleGuide(true) //Opens list
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
//                if (listSize > 0) {
//                    val next = if (currentIndex > 0) currentIndex - 1 else listSize - 1
//                    playChannelAt(next)
//                }
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
//                if (listSize > 0) {
//                    val next = (currentIndex + 1) % listSize
//                    playChannelAt(next)
//                }
//                true
//            }
//            else -> super.onKeyDown(keyCode, event)
//        }
//    }
//
//    //This function creates and acquires a Multicast Lock to enable your Android TV device
//    //to receive UDP multicast streams (like your MPEG-TS over UDP live channels).
//    private fun setupMulticastLock() {
//        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        multicastLock = wm.createMulticastLock("iptv_multicast_lock").apply {
//            setReferenceCounted(true)
//            acquire()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        player?.release()
//        // Disable multicast to save battery
//        if (multicastLock?.isHeld == true) multicastLock?.release()
//    }
//
//    private inner class GuideAdapter(context: Context, items: List<Channel>) : ArrayAdapter<Channel>(context, 0, items) {
//        override fun getView(pos: Int, conv: View?, parent: ViewGroup): View {
//            val v = conv ?: View.inflate(context, R.layout.item_channel, null)
//            val item = getItem(pos)!!
//
//            val name = v.findViewById<TextView>(R.id.channelName)
//            val sub = v.findViewById<TextView>(R.id.channelUrl)
//
//            name.text = item.name
//
//            if (pos == currentIndex) {
//                sub.text = "▶ NOW PLAYING"
//                sub.setTextColor(Color.parseColor("#00E5FF"))
//            } else {
//                sub.text = "CH ${pos + 1} • LIVE"
//                sub.setTextColor(Color.parseColor("#88FFFFFF"))
//            }
//            return v
//        }
//    }
//}

///************ fast zipping **************/


package com.example.iptv

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.OptIn
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.ui.PlayerView
import java.net.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

@OptIn(UnstableApi::class)
class LivePlayerActivity : BaseActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var sideGuidePanel: View
    private lateinit var guideOverlay: View
    private lateinit var guideListView: ListView
    private lateinit var channelOverlay: TextView
    private lateinit var miniInfoBar: View
    private lateinit var miniChannelName: TextView
    private lateinit var miniChannelNumber: TextView

    private var currentIndex = 0
    private lateinit var adapter: GuideAdapter
    private var multicastLock: WifiManager.MulticastLock? = null

    @Volatile private var sapRunning = false
    private var sapThread: Thread? = null
    private val zapToken = AtomicInteger(0)
    private val uiHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_player)

        bindViews()
        setupMulticastLock()
        initPlayer()
        setupGuide()
        startSapListener()
    }

    private fun bindViews() {
        playerView = findViewById(R.id.exoPlayerView)
        sideGuidePanel = findViewById(R.id.sideGuidePanel)
        guideOverlay = findViewById(R.id.guideOverlay)
        guideListView = findViewById(R.id.guideListView)
        channelOverlay = findViewById(R.id.channelOverlay)
        miniInfoBar = findViewById(R.id.miniInfoBar)
        miniChannelName = findViewById(R.id.miniChannelName)
        miniChannelNumber = findViewById(R.id.miniChannelNumber)
    }

    // ---------------- FFmpeg & Player Initialization ----------------
    private fun initPlayer() {
        val renderersFactory = DefaultRenderersFactory(this).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        }

        // Standard way to build LoadControl in Media3 1.4.1
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                2500,  // Min buffer
                5000,  // Max buffer
                1000,  // Buffer for playback
                1500   // Buffer for rebuffering
            )
            .build()

        player = ExoPlayer.Builder(this)
            .setRenderersFactory(renderersFactory)
            .setLoadControl(loadControl)
            .build()

        playerView.player = player
    }

    private fun playUrl(url: String, name: String) {
        val myToken = zapToken.incrementAndGet()

        // Clean URL (Media3 doesn't like the @ symbol in standard Uri parsing sometimes)
        val fixedUrl = url.replace("udp://@", "udp://")
        val uri = Uri.parse(fixedUrl)

        // 3. TS Extractor configuration to handle raw IPTV packets
        val extractorsFactory = DefaultExtractorsFactory().apply {
            setTsExtractorFlags(
                DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                        DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
            )
        }

        player?.apply {
            stop()
            clearMediaItems()
            val ds = DefaultDataSource.Factory(this@LivePlayerActivity)
            val ms = ProgressiveMediaSource.Factory(ds, extractorsFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            setMediaSource(ms)
            prepare()
            playWhenReady = true
        }

        showOverlay(name)
        showMiniInfo(name, currentIndex + 1)
        adapter.notifyDataSetChanged()
    }

    // ---------------- Channel Logic & SAP ----------------
    private fun playChannel(index: Int) {
        val list = ChannelManager.channels
        if (list.isEmpty()) return
        currentIndex = if (index < 0) list.lastIndex else if (index >= list.size) 0 else index
        val ch = list[currentIndex]
        playUrl(ch.url, ch.name)
    }

    private fun startSapListener() {
        sapRunning = true
        sapThread = thread(start = true, name = "SapListener") {
            var socket: MulticastSocket? = null
            try {
                val group = InetAddress.getByName("224.2.127.254")
                val nif = findBestInterface() ?: return@thread
                socket = MulticastSocket(9875).apply {
                    reuseAddress = true
                    networkInterface = nif
                    joinGroup(InetSocketAddress(group, 9875), nif)
                }
                val buffer = ByteArray(65535)
                while (sapRunning) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val msg = String(packet.data, 0, packet.length)
                    val name = Regex("""(?m)^s=(.+)$""").find(msg)?.groupValues?.get(1)?.trim()
                    val ip = Regex("""(?m)^c=IN IP4\s+([0-9.]+)""").find(msg)?.groupValues?.get(1)
                    val port = Regex("""(?m)^m=video\s+(\d+)""").find(msg)?.groupValues?.get(1)

                    if (name != null && ip != null && port != null) {
                        val url = "udp://@$ip:$port"
                        runOnUiThread {
                            if (!ChannelManager.channels.any { it.url == url }) {
                                ChannelManager.channels.add(Channel(name, url))
                                adapter.notifyDataSetChanged()
                                if (ChannelManager.channels.size == 1) playChannel(0)
                            }
                        }
                    }
                }
            } catch (e: Exception) { Log.e("SAP", "Error: ${e.message}") }
            finally { socket?.close() }
        }
    }

    private fun findBestInterface(): NetworkInterface? {
        return NetworkInterface.getNetworkInterfaces()?.toList()
            ?.firstOrNull { it.isUp && it.supportsMulticast() && !it.isLoopback }
    }

    // ---------------- UI Navigation ----------------
    private fun setupGuide() {
        adapter = GuideAdapter(this, ChannelManager.channels)
        guideListView.adapter = adapter
        guideListView.setOnItemClickListener { _, _, position, _ ->
            playChannel(position)
            toggleGuide(false)
        }
    }

    private fun toggleGuide(show: Boolean) {
        sideGuidePanel.visibility = if (show) View.VISIBLE else View.GONE
        guideOverlay.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            guideListView.requestFocus()
            guideListView.setSelection(currentIndex)
        }
    }

    private fun showOverlay(text: String) {
        channelOverlay.text = text
        channelOverlay.visibility = View.VISIBLE
        uiHandler.removeCallbacks(hideOverlayRunnable)
        uiHandler.postDelayed(hideOverlayRunnable, 2500)
    }
    private val hideOverlayRunnable = Runnable { channelOverlay.visibility = View.GONE }

    private fun showMiniInfo(name: String, number: Int) {
        miniChannelName.text = name
        miniChannelNumber.text = number.toString()
        miniInfoBar.visibility = View.VISIBLE
        uiHandler.removeCallbacks(hideMiniBarRunnable)
        uiHandler.postDelayed(hideMiniBarRunnable, 4000)
    }
    private val hideMiniBarRunnable = Runnable { miniInfoBar.visibility = View.GONE }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (sideGuidePanel.visibility == View.VISIBLE) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { toggleGuide(false); return true }
            return super.onKeyDown(keyCode, event)
        }
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { toggleGuide(true); return true }
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> { playChannel(currentIndex - 1); return true }
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> { playChannel(currentIndex + 1); return true }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setupMulticastLock() {
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wm.createMulticastLock("iptv_lock").apply {
            setReferenceCounted(false)
            try { acquire() } catch (_: Exception) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sapRunning = false
        player?.release()
        multicastLock?.let { if (it.isHeld) it.release() }
    }

    private inner class GuideAdapter(context: Context, items: List<Channel>) : ArrayAdapter<Channel>(context, 0, items) {
        override fun getView(pos: Int, conv: View?, parent: ViewGroup): View {
            val v = conv ?: View.inflate(context, R.layout.item_channel, null)
            val item = getItem(pos)!!
            v.findViewById<TextView>(R.id.channelName).text = item.name
            val sub = v.findViewById<TextView>(R.id.channelUrl)
            if (pos == currentIndex) {
                sub.text = "▶ NOW PLAYING"
                sub.setTextColor(Color.parseColor("#00E5FF"))
                v.setBackgroundColor(Color.parseColor("#3300E5FF"))
            } else {
                sub.text = "CH ${pos + 1}"
                sub.setTextColor(Color.GRAY)
                v.setBackgroundColor(Color.TRANSPARENT)
            }
            return v
        }
    }
}