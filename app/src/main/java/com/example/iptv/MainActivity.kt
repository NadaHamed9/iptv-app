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

class MainActivity : BaseActivity() {

    private var isPlaying = false
    private lateinit var playOverlay: ImageView
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        updateDateTime()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)
        val guestName = prefs.getString("guest_name", "John Tyler")
        welcomeText.text = getString(R.string.welcome_name, guestName)
    }

    private fun setupVideoPlayer() {
        val videoView = findViewById<VideoView>(R.id.hotelVideoView)
        val container = findViewById<View>(R.id.btnLiveTv)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.hotel_ad
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.setOnPreparedListener { it.seekTo(1) }

        container.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start()
                v.z = 10f
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                v.z = 0f
            }
        }

        container.setOnClickListener {
            if (isPlaying) {
                videoView.pause()
                playOverlay.visibility = View.VISIBLE
            } else {
                videoView.start()
                playOverlay.visibility = View.GONE
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupSmallTile(viewId: Int, label: String, iconRes: Int) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<TextView>(R.id.tileLabel).text = label
        view.findViewById<ImageView>(R.id.tileIcon).setImageResource(iconRes)

        view.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200).start()
                v.z = 10f
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                v.z = 0f
            }
        }

        view.setOnClickListener {
            when (viewId) {
                R.id.btnSettings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.btnMovies -> startActivity(Intent(this, MoviesActivity::class.java)) // Added this line
                else -> Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomTile(viewId: Int, label: String, imgRes: Int) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<TextView>(R.id.tileLabel).text = label
        view.findViewById<ImageView>(R.id.tileImage).setImageResource(imgRes)

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
                R.id.btnRoomService -> startActivity(Intent(this, RoomServiceActivity::class.java))
                R.id.btnHotelInfo -> startActivity(Intent(this, HotelInfoActivity::class.java))
                else -> Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDateTime() {
        val dateText = findViewById<TextView>(R.id.dateText)
        val sdf = SimpleDateFormat("EEE dd MMM yyyy HH:mm", Locale.getDefault())
        dateText.text = sdf.format(Date())
    }
}