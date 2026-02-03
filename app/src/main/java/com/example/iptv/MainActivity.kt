package com.example.iptv

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge

class MainActivity : BaseActivity() {

    private var isPlaying = false
    private lateinit var playOverlay: ImageView
    private lateinit var welcomeText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //enableEdgeToEdge()

        playOverlay = findViewById(R.id.playOverlay)
        welcomeText = findViewById(R.id.welcomeText)

        setupSmallTile(R.id.btnTvGuide, getString(R.string.tv_guide), R.drawable.ic_tv)
        setupSmallTile(R.id.btnMovies, getString(R.string.movies), R.drawable.ic_movie)
        setupSmallTile(R.id.btnMyBill, getString(R.string.my_bill), R.drawable.ic_bill)
        setupSmallTile(R.id.btnMessages, getString(R.string.messages), R.drawable.ic_messages)
        setupSmallTile(R.id.btnSettings, getString(R.string.settings), R.drawable.ic_settings)
        setupSmallTile(R.id.btnRadio, getString(R.string.radio), R.drawable.ic_radio)

        setupBottomTile(R.id.btnRoomService, getString(R.string.room_service), R.drawable.room_service_preview)
        setupBottomTile(R.id.btnHotelInfo, getString(R.string.hotel_info), R.drawable.hotel_info_preview)
        setupBottomTile(R.id.btnConcierge, getString(R.string.concierge), R.drawable.concierge_preview)
        setupBottomTile(R.id.btnSpa, getString(R.string.spa), R.drawable.spa_wellness_preview)

        setupVideoPlayer()
        setupDateTimeAutoUpdate()
        // START DISCOVERY IMMEDIATELY
        ChannelManager.startDiscovery()
    }

    override fun onResume() {
        super.onResume()
        // Start the clock when user returns to the app
        handler.post(updateTimeRunnable)
        val prefs = getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)
        val guestName = prefs.getString("guest_name", "John Tyler")
        welcomeText.text = getString(R.string.welcome_name, guestName)
    }

    override fun onPause() {
        super.onPause()
        // Stop the clock when app is in background to save battery
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun setupVideoPlayer() {
        val videoView = findViewById<VideoView>(R.id.hotelVideoView)
        val container = findViewById<View>(R.id.btnHotelAd)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.hotel_ad
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.setOnPreparedListener { it.seekTo(1) }

        container.setOnClickListener {
            // If video is playing,and i click it then pause it
            if (isPlaying) {
                videoView.pause()
                playOverlay.visibility = View.VISIBLE // Show play button overlay
            } else {
                videoView.start()
                playOverlay.visibility = View.GONE // Hide play button overlay
            }
            isPlaying = !isPlaying // Toggle the state
        }
    }

    private fun setupSmallTile(viewId: Int, label: String, iconRes: Int) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<TextView>(R.id.tileLabel).text = label
        view.findViewById<ImageView>(R.id.tileIcon).setImageResource(iconRes)

        view.setOnClickListener {
            when (viewId) {
                R.id.btnTvGuide -> {
                    val intent = Intent(this, LivePlayerActivity::class.java)
                    startActivity(intent)
                }
                R.id.btnSettings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.btnMovies -> startActivity(Intent(this, MoviesActivity::class.java))
                else -> Toast.makeText(this, "$label feature coming soon!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomTile(viewId: Int, label: String, imgRes: Int) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<TextView>(R.id.tileLabel).text = label
        view.findViewById<ImageView>(R.id.tileImage).setImageResource(imgRes)
        //to zoom when click on the icons
        view.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                v.z = 10f
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                v.z = 0f
            }
        }

        view.setOnClickListener {
            when (viewId) {
                R.id.btnRoomService -> {
                    startActivity(Intent(this, RoomServiceActivity::class.java))
                }
                R.id.btnHotelInfo -> {
                    startActivity(Intent(this, HotelInfoActivity::class.java))
                }
                R.id.btnConcierge -> {
                    val intent = Intent(this, ConciergeActivity::class.java)
                    startActivity(intent)
                }
                R.id.btnSpa -> {
                    startActivity(Intent(this, SpaActivity::class.java))
                }
                else -> {
                    Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupDateTimeAutoUpdate() {
        val dateText = findViewById<TextView>(R.id.dateText)
        val timeText = findViewById<TextView>(R.id.timeText)

        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val cairoZone = TimeZone.getTimeZone("Africa/Cairo")
        dateFormat.timeZone = cairoZone
        timeFormat.timeZone = cairoZone

        updateTimeRunnable = object : Runnable {
            override fun run() {
                val now = Date()
                dateText.text = dateFormat.format(now)
                timeText.text = timeFormat.format(now)

                // Loop every second
                handler.postDelayed(this, 1000)
            }
        }

        // Trigger the first update immediately so it's ready before the loop starts
        val now = Date()
        dateText.text = dateFormat.format(now)
        timeText.text = timeFormat.format(now)
    }
}