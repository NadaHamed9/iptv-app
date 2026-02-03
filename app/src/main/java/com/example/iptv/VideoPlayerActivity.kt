//package com.example.iptv
//
//import android.os.Bundle
//import android.view.View
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
//    private val dashUrl = "http://10.10.10.115:8081/hls/movie4.mp4/manifest.mpd"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video_player)
//
//        playerView = findViewById(R.id.exoPlayerView)
//        loadingSpinner = findViewById(R.id.loadingSpinner)
//        resolutionText = findViewById(R.id.resolutionText)
//
//        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: dashUrl
//        initializePlayer(videoUrl)
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun initializePlayer(url: String) {
//        val renderersFactory = DefaultRenderersFactory(this)
//            .setEnableDecoderFallback(true)
//
//        // LIMIT RESOLUTION: Prevents the Arris STB 1440p crash
//        val trackSelector = DefaultTrackSelector(this)
//        trackSelector.parameters = trackSelector.buildUponParameters()
//            .setMaxVideoSize(1920, 1080)
//            .build()
//
//        // AUDIO CONFIG: Required for Audio-Video synchronization
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
//        // The 'true' parameter in setAudioAttributes handles audio focus automatically.
//        val newPlayer = ExoPlayer.Builder(this, renderersFactory)
//            .setTrackSelector(trackSelector)
//            .setLoadControl(loadControl)
//            .setAudioAttributes(audioAttributes, true)
//            .build()
//
//        player = newPlayer
//        playerView.player = newPlayer
//
//        // Ensure playback speed is strictly normal (1.0x)
//        newPlayer.setPlaybackSpeed(1.0f)
//
//        val mediaItem = MediaItem.Builder()
//            .setUri(url)
//            .setMimeType(MimeTypes.APPLICATION_MPD)
//            .build()
//
//        newPlayer.setMediaItem(mediaItem)
//        newPlayer.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(state: Int) {
//                loadingSpinner.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
//                if (state == Player.STATE_READY) {
//                    newPlayer.play()
//                }
//            }
//
//            override fun onVideoSizeChanged(videoSize: VideoSize) {
//                resolutionText.text = "Playing: ${videoSize.width}x${videoSize.height}"
//            }
//
//            override fun onPlayerError(error: PlaybackException) {
//                handleHardwareError(error)
//            }
//        })
//
//        newPlayer.prepare()
//        newPlayer.playWhenReady = true
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
//            val videoUrl = intent.getStringExtra("VIDEO_URL") ?: dashUrl
//            initializePlayer(videoUrl)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        player?.pause()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        releasePlayer()
//    }
//
//    private fun releasePlayer() {
//        player?.release()
//        player = null
//    }
//}

package com.example.iptv

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView // Added
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.view.ContextThemeWrapper
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TrackSelectionDialogBuilder

@OptIn(UnstableApi::class)
class VideoPlayerActivity : BaseActivity() {

    private var player: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private lateinit var playerView: PlayerView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var resolutionText: TextView // 1. Added variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.exoPlayerView)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        resolutionText = findViewById(R.id.resolutionText) // 2. Initialize TextView

        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: "http://10.10.11.47:8081/abr/movie1/movie1.smil/manifest.mpd"

        initializePlayer(videoUrl)
    }

    private fun initializePlayer(url: String) {
        trackSelector = DefaultTrackSelector(this)

        // Safety: Limit Arris STB to 1080p
        trackSelector?.parameters = trackSelector?.buildUponParameters()
            ?.setMaxVideoSize(1920, 1080)
            ?.build()!!

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector!!)
            .build().also { exoPlayer ->
                playerView.player = exoPlayer

                val mediaItem = MediaItem.Builder()
                    .setUri(url)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true

                // 3. Updated Listener to track video size changes
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        loadingSpinner.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                        if (state == Player.STATE_READY) {
                            setupSettingsButton() // Updated name
                        }
                    }

                    // This function triggers every time the resolution changes
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        super.onVideoSizeChanged(videoSize)
                        if (videoSize.height > 0) {
                            val qualityLabel = when {
                                videoSize.height >= 1080 -> "1080p (FHD)"
                                videoSize.height >= 720 -> "720p (HD)"
                                videoSize.height >= 480 -> "480p (SD)"
                                else -> "${videoSize.height}p"
                            }
                            resolutionText.text = "Quality: $qualityLabel"
                        }
                    }
                })
            }
    }

    private fun setupSettingsButton() {
        // Find the built-in gear icon
        val settingsBtn = playerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_settings)

        settingsBtn?.setOnClickListener {
            val currentPlayer = player ?: return@setOnClickListener
            val contextWrapper = ContextThemeWrapper(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)

            // 1. Create a "Main Menu" with 3 options
            val options = arrayOf("Video Quality", "Audio Tracks", "Playback Speed")

            androidx.appcompat.app.AlertDialog.Builder(contextWrapper)
                .setTitle("Settings")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showQualityMenu(currentPlayer, contextWrapper)
                        1 -> showAudioMenu(currentPlayer, contextWrapper)
                        2 -> showSpeedMenu(currentPlayer, contextWrapper)
                    }
                }
                .show()
        }
    }

    // --- OPTION 1: VIDEO QUALITY ---
    private fun showQualityMenu(currentPlayer: Player, context: ContextThemeWrapper) {
        TrackSelectionDialogBuilder(context, "Select Quality", currentPlayer, C.TRACK_TYPE_VIDEO)
            .setAllowAdaptiveSelections(false) // Single choice only as you requested
            .setShowDisableOption(false)
            .build()
            .show()
    }

    // --- OPTION 2: AUDIO TRACKS ---
    private fun showAudioMenu(currentPlayer: Player, context: ContextThemeWrapper) {
        TrackSelectionDialogBuilder(context, "Select Audio Language", currentPlayer, C.TRACK_TYPE_AUDIO)
            .setAllowAdaptiveSelections(false)
            .setShowDisableOption(false)
            .build()
            .show()
    }

    // --- OPTION 3: PLAYBACK SPEED ---
    private fun showSpeedMenu(currentPlayer: Player, context: ContextThemeWrapper) {
        val speeds = arrayOf("0.5x", "0.75x", "Normal", "1.25x", "1.5x", "2.0x")
        val speedValues = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Select Speed")
            .setItems(speeds) { _, which ->
                val param = PlaybackParameters(speedValues[which])
                currentPlayer.playbackParameters = param
                Toast.makeText(this, "Speed set to ${speeds[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}