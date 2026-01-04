package com.example.iptv

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MoviesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        setupBackButton()

        // Pass posters here
        setupMovieTile(R.id.movie1, "Avatar Fire And Ash", "http://10.10.10.115:8081/hls/movie2.mp4/manifest.mpd", R.drawable.poster_avatar_fire_and_ash)
        setupMovieTile(R.id.movie2, "The Odyssey", "http://10.10.10.115:8081/hls/movie3.mp4/manifest.mpd", R.drawable.poster_the_odessey)
        setupMovieTile(R.id.movie3, "A House Of Dynamite", "http://10.10.10.115:8081/hls/movie4.mp4/manifest.mpd", R.drawable.poster_a_house_of_dynamite)
    }

    private fun setupMovieTile(viewId: Int, title: String, videoUrl: String, imageRes: Int) {
        val container = findViewById<View>(viewId) ?: return

        val label = container.findViewById<TextView>(R.id.movieTitle)
        val poster = container.findViewById<ImageView>(R.id.moviePoster)

        label.text = title
        poster.setImageResource(imageRes)

        // 1. Handle Click (Launch Video)
        container.setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", videoUrl)
            startActivity(intent)
        }

        // 2. Handle Focus (Zoom and Glow Effect)
        container.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Zoom in effect
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()

                // Increase elevation to bring it to the front
                view.elevation = 20f

                // If you have a background selector, this triggers the cyan border
                view.isActivated = true
            } else {
                // Zoom back to normal
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()

                // Return to normal elevation
                view.elevation = 8f

                view.isActivated = false
            }
        }
    }
    private fun setupBackButton() {
        val backBtn = findViewById<ImageView>(R.id.btnBack) ?: return
        backBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)
        backBtn.setOnClickListener { finish() }

        backBtn.setOnFocusChangeListener { view, hasFocus ->
            val imageView = view as ImageView
            if (hasFocus) {
                imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#00E5FF"))
                imageView.scaleX = 1.15f
                imageView.scaleY = 1.15f
            } else {
                imageView.imageTintList = ColorStateList.valueOf(Color.WHITE)
                imageView.scaleX = 1.0f
                imageView.scaleY = 1.0f
            }
        }
    }
}

/*******************/

//many resolution version
//package com.example.iptv
//
//import android.content.Intent
//import android.content.res.ColorStateList
//import android.graphics.Color
//import android.os.Bundle
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import java.util.HashMap
//
//class MoviesActivity : BaseActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_movies)
//
//        setupBackButton()
//
//        // --- Movie 1: Avatar (Single Resolution) ---
//        setupMovieTile(
//            R.id.movie1,
//            "Avatar Fire And Ash",
//            mapOf("Auto" to "http://10.10.10.115:8081/hls/movie2.mp4/manifest.mpd"),
//            R.drawable.poster_avatar_fire_and_ash
//        )
//
//        // --- Movie 2: The Odyssey (Single Resolution) ---
//        setupMovieTile(
//            R.id.movie2,
//            "The Odyssey",
//            mapOf("Auto" to "http://10.10.10.115:8081/hls/movie3.mp4/manifest.mpd"),
//            R.drawable.poster_the_odessey
//        )
//
//        // --- Movie 3: A House Of Dynamite (MULTIPLE RESOLUTIONS) ---
//        setupMovieTile(
//            R.id.movie3,
//            "A House Of Dynamite",
//            mapOf(
//                "Auto"  to "http://10.10.10.115:8081/hls/movie4.mp4/manifest.mpd",
//                "1080p" to "http://10.10.10.115:8081/hls/movie1.mp4/manifest.mpd",
//                "360p"  to "http://10.10.10.115:8081/hls/movie4.mp4/manifest.mpd"
//            ),
//            R.drawable.poster_a_house_of_dynamite
//        )
//    }
//
//    private fun setupMovieTile(viewId: Int, title: String, urlMap: Map<String, String>, imageRes: Int) {
//        val container = findViewById<View>(viewId) ?: return
//
//        val label = container.findViewById<TextView>(R.id.movieTitle)
//        val poster = container.findViewById<ImageView>(R.id.moviePoster)
//
//        label.text = title
//        poster.setImageResource(imageRes)
//
//        // Handle Click - Pass the Resolution Map to the Player
//        container.setOnClickListener {
//            val intent = Intent(this, VideoPlayerActivity::class.java)
//            // We pass the map as a Serializable HashMap so the PlayerActivity can read it
//            intent.putExtra("VIDEO_URL_MAP", HashMap(urlMap))
//            startActivity(intent)
//        }
//
//        // Handle Focus (Zoom and Glow Effect)
//        container.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) {
//                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
//                view.elevation = 20f
//                view.isActivated = true
//            } else {
//                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
//                view.elevation = 8f
//                view.isActivated = false
//            }
//        }
//    }
//
//    private fun setupBackButton() {
//        val backBtn = findViewById<ImageView>(R.id.btnBack) ?: return
//        backBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)
//        backBtn.setOnClickListener { finish() }
//
//        backBtn.setOnFocusChangeListener { view, hasFocus ->
//            val imageView = view as ImageView
//            if (hasFocus) {
//                imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#00E5FF"))
//                imageView.scaleX = 1.15f
//                imageView.scaleY = 1.15f
//            } else {
//                imageView.imageTintList = ColorStateList.valueOf(Color.WHITE)
//                imageView.scaleX = 1.0f
//                imageView.scaleY = 1.0f
//            }
//        }
//    }
//}