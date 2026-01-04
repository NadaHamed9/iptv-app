package com.example.iptv

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class HotelInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_info)

        setupBackButton()

        // Setting up tiles matching your Room Service template IDs
        setupCategoryTile(R.id.btnFacilities, getString(R.string.label_facilities), R.drawable.ic_facilities)
        setupCategoryTile(R.id.btnWifi, getString(R.string.label_wifi), R.drawable.ic_wifi)
        setupCategoryTile(R.id.btnDining, getString(R.string.label_dining_hours), R.drawable.ic_dining_clock)
        setupCategoryTile(R.id.btnLocalGuide, getString(R.string.label_local_guide), R.drawable.ic_location)
        setupCategoryTile(R.id.btnSafety, getString(R.string.label_safety), R.drawable.ic_safety)
        setupCategoryTile(R.id.btnCheckout, getString(R.string.label_checkout), R.drawable.ic_checkout)
    }

    private fun setupCategoryTile(viewId: Int, label: String, iconRes: Int) {
        val container = findViewById<View>(viewId) ?: return

        // IDs must match categoryIcon and categoryLabel in item_menu_category.xml
        val textView = container.findViewById<TextView>(R.id.categoryLabel)
        val imageView = container.findViewById<ImageView>(R.id.categoryIcon)

        textView?.text = label
        imageView?.setImageResource(iconRes)

        // Focus listener removed to match RoomService code.
        // The "Glow" is handled by the XML background selector.

        container.setOnClickListener {
            Toast.makeText(this, "Opening $label...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackButton() {
        val backBtn = findViewById<ImageView>(R.id.btnBack) ?: return

        backBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)
        backBtn.setOnClickListener { finish() }

        // Back button maintains focus effect to match your RoomService style
        backBtn.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            val imageView = view as ImageView
            if (hasFocus) {
                // Selected: Bright Cyan glow
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