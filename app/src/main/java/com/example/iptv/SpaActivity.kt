package com.example.iptv

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView // Correct: Match the XML type
import android.widget.FrameLayout
import android.widget.Toast

class SpaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spa)

        setupBackButton()

        setupTiles()
    }

    private fun setupBackButton() {
        val backBtn = findViewById<ImageView>(R.id.btnBack) ?: return

        // Set initial color to White
        backBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)

        backBtn.setOnClickListener {
            finish()
        }

        backBtn.setOnFocusChangeListener { view, hasFocus ->
            val imageView = view as ImageView
            if (hasFocus) {
                // Selected: Bright Cyan
                imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#00E5FF"))
                imageView.scaleX = 1.15f
                imageView.scaleY = 1.15f
            } else {
                // Not Selected: Pure White
                imageView.imageTintList = ColorStateList.valueOf(Color.WHITE)
                imageView.scaleX = 1.0f
                imageView.scaleY = 1.0f
            }
        }
    }


    private fun setupTiles() {
        val massageTile = findViewById<FrameLayout>(R.id.itemMassage)
        val facialTile = findViewById<FrameLayout>(R.id.itemFacial)
        val salonTile = findViewById<FrameLayout>(R.id.itemSalon)
        val gymTile = findViewById<FrameLayout>(R.id.itemGym)

        massageTile.setOnClickListener {
            Toast.makeText(this, "Massage selected", Toast.LENGTH_SHORT).show()
        }

        facialTile.setOnClickListener {
            Toast.makeText(this, "Facial selected", Toast.LENGTH_SHORT).show()
        }

        salonTile.setOnClickListener {
            Toast.makeText(this, "Salon selected", Toast.LENGTH_SHORT).show()
        }

        gymTile.setOnClickListener {
            Toast.makeText(this, "GYM selected", Toast.LENGTH_SHORT).show()
        }

        // Standard focus for TV remote
        massageTile.requestFocus()
    }
}