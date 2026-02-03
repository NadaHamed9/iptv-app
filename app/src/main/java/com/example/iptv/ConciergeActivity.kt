package com.example.iptv

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ConciergeActivity :  BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concierge)

        setupBackButton()

        setupTile(R.id.itemCheckOut, getString(R.string.check_out), R.drawable.ic_exit)
        setupTile(R.id.itemWakeup, getString(R.string.wake_up_call), R.drawable.ic_alarm)
        setupTile(R.id.itemTransport, getString(R.string.transportation), R.drawable.ic_taxi)
        setupTile(R.id.itemLuggage, getString(R.string.luggage_help), R.drawable.ic_luggage)
        setupTile(R.id.itemMaintenance, getString(R.string.maintenance), R.drawable.ic_maintenance)
        setupTile(R.id.itemFrontDesk, getString(R.string.call_front_desk), R.drawable.ic_phone)
    }

    private fun setupTile(layoutId: Int, title: String, iconRes: Int) {
        val tile = findViewById<View>(layoutId) ?: return

        // UPDATED: Now using your exact IDs: categoryLabel and categoryIcon
        val titleView = tile.findViewById<TextView>(R.id.categoryLabel)
        val iconView = tile.findViewById<ImageView>(R.id.categoryIcon)

        if (titleView != null && iconView != null) {
            titleView.text = title
            iconView.setImageResource(iconRes)
        }

        tile.setOnClickListener {
            // Add your navigation or action here
        }
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
}