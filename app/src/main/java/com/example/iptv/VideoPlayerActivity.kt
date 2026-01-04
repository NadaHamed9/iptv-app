package com.example.iptv

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : BaseActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var resolutionText: TextView
    private val dashUrl = "http://10.10.10.115:8081/hls/movie4.mp4/manifest.mpd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.exoPlayerView)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        resolutionText = findViewById(R.id.resolutionText)

        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: dashUrl
        initializePlayer(videoUrl)
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        val renderersFactory = DefaultRenderersFactory(this)
            .setEnableDecoderFallback(true)

        // LIMIT RESOLUTION: Prevents the Arris STB 1440p crash
        val trackSelector = DefaultTrackSelector(this)
        trackSelector.parameters = trackSelector.buildUponParameters()
            .setMaxVideoSize(1920, 1080)
            .build()

        // AUDIO CONFIG: Required for Audio-Video synchronization
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(30000, 60000, 3000, 5000)
            .build()

        // Build player
        // The 'true' parameter in setAudioAttributes handles audio focus automatically.
        val newPlayer = ExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .build()

        player = newPlayer
        playerView.player = newPlayer

        // Ensure playback speed is strictly normal (1.0x)
        newPlayer.setPlaybackSpeed(1.0f)

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        newPlayer.setMediaItem(mediaItem)
        newPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                loadingSpinner.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                if (state == Player.STATE_READY) {
                    newPlayer.play()
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                resolutionText.text = "Playing: ${videoSize.width}x${videoSize.height}"
            }

            override fun onPlayerError(error: PlaybackException) {
                handleHardwareError(error)
            }
        })

        newPlayer.prepare()
        newPlayer.playWhenReady = true
    }

    private fun handleHardwareError(error: PlaybackException) {
        loadingSpinner.visibility = View.GONE
        val message = when (error.errorCode) {
            PlaybackException.ERROR_CODE_DECODING_FAILED -> "Hardware Limit: Resolution not supported."
            else -> "Error: ${error.localizedMessage}"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            val videoUrl = intent.getStringExtra("VIDEO_URL") ?: dashUrl
            initializePlayer(videoUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}
/*******************************************/
//many resolution version using manually settings icon but .xml file feh moshkla 
//package com.example.iptv
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.View
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.OptIn
//import androidx.media3.common.AudioAttributes
//import androidx.media3.common.C
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MimeTypes
//import androidx.media3.common.Player
//import androidx.media3.common.VideoSize
//import androidx.media3.common.PlaybackException
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.DefaultLoadControl
//import androidx.media3.exoplayer.DefaultRenderersFactory
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
//import androidx.media3.ui.PlayerView
//
//class VideoPlayerActivity : BaseActivity() {
//
//    private var player: ExoPlayer? = null
//    private lateinit var playerView: PlayerView
//    private lateinit var loadingSpinner: ProgressBar
//    private lateinit var resolutionText: TextView
//    private lateinit var settingsBtn: ImageView
//
//    private var urlMap: HashMap<String, String>? = null
//    private var currentResolution = "Auto"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video_player)
//
//        playerView = findViewById(R.id.exoPlayerView)
//        loadingSpinner = findViewById(R.id.loadingSpinner)
//        resolutionText = findViewById(R.id.resolutionText)
//        settingsBtn = findViewById(R.id.settingsBtn)
//
//        // 1. Get the resolution map from the Intent
//        urlMap = intent.getSerializableExtra("VIDEO_URL_MAP") as? HashMap<String, String>
//
//        // 2. Start with "Auto" or the first available resolution
//        val initialUrl = urlMap?.get("Auto") ?: urlMap?.values?.firstOrNull() ?: ""
//        initializePlayer(initialUrl)
//
//        // 3. Setup settings button for resolution switching
//        settingsBtn.setOnClickListener { showResolutionDialog() }
//    }
//
//    private fun showResolutionDialog() {
//        if (urlMap == null || urlMap!!.size <= 1) {
//            Toast.makeText(this, "No other resolutions available", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val resolutions = urlMap!!.keys.toTypedArray()
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Select Resolution")
//        builder.setItems(resolutions) { _, which ->
//            val selectedRes = resolutions[which]
//            if (selectedRes != currentResolution) {
//                switchResolution(selectedRes)
//            }
//        }
//        builder.show()
//    }
//
//    private fun switchResolution(newRes: String) {
//        val newUrl = urlMap?.get(newRes) ?: return
//
//        // Save current position so the movie doesn't restart
//        val currentPosition = player?.currentPosition ?: 0L
//        currentResolution = newRes
//
//        val mediaItem = MediaItem.Builder()
//            .setUri(newUrl)
//            .setMimeType(MimeTypes.APPLICATION_MPD)
//            .build()
//
//        player?.setMediaItem(mediaItem)
//        player?.prepare()
//        player?.seekTo(currentPosition) // Resume at the exact same second
//        player?.play()
//
//        resolutionText.text = "Quality: $newRes"
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun initializePlayer(url: String) {
//        val renderersFactory = DefaultRenderersFactory(this)
//            .setEnableDecoderFallback(true)
//
//        // LIMIT RESOLUTION: Prevents Arris STB 1440p crashes
//        val trackSelector = DefaultTrackSelector(this)
//        trackSelector.parameters = trackSelector.buildUponParameters()
//            .setMaxVideoSize(1920, 1080)
//            .build()
//
//        // SPEED FIX: Setup Audio Attributes so video waits for the audio hardware clock
//        val audioAttributes = AudioAttributes.Builder()
//            .setUsage(C.USAGE_MEDIA)
//            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
//            .build()
//
//        val loadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(30000, 60000, 3000, 5000)
//            .build()
//
//        // Build player
//        player = ExoPlayer.Builder(this, renderersFactory)
//            .setTrackSelector(trackSelector)
//            .setLoadControl(loadControl)
//            .setAudioAttributes(audioAttributes, true) // Second parameter 'true' handles focus automatically
//            .build().also { exoPlayer ->
//                playerView.player = exoPlayer
//
//                // Force normal playback speed
//                exoPlayer.setPlaybackSpeed(1.0f)
//
//                val mediaItem = MediaItem.Builder()
//                    .setUri(url)
//                    .setMimeType(MimeTypes.APPLICATION_MPD)
//                    .build()
//
//                exoPlayer.setMediaItem(mediaItem)
//                exoPlayer.addListener(object : Player.Listener {
//                    override fun onPlaybackStateChanged(state: Int) {
//                        loadingSpinner.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
//                    }
//
//                    override fun onVideoSizeChanged(videoSize: VideoSize) {
//                        // Only update if we are in "Auto" mode to show the actual hardware resolution
//                        if (currentResolution == "Auto") {
//                            resolutionText.text = "Quality: Auto (${videoSize.height}p)"
//                        }
//                    }
//
//                    override fun onPlayerError(error: PlaybackException) {
//                        handleHardwareError(error)
//                    }
//                })
//
//                exoPlayer.prepare()
//                exoPlayer.playWhenReady = true
//            }
//    }
//
//    private fun handleHardwareError(error: PlaybackException) {
//        loadingSpinner.visibility = View.GONE
//        val message = when (error.errorCode) {
//            PlaybackException.ERROR_CODE_DECODING_FAILED -> "Hardware Limit: Resolution not supported."
//            else -> "Error: ${error.localizedMessage}"
//        }
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (player == null) {
//            val initialUrl = urlMap?.get(currentResolution) ?: urlMap?.get("Auto") ?: ""
//            initializePlayer(initialUrl)
//        }
//    }
//
//    override fun onPause() { super.onPause(); player?.pause() }
//    override fun onStop() { super.onStop(); releasePlayer() }
//
//    private fun releasePlayer() {
//        player?.release()
//        player = null
//    }
//}}