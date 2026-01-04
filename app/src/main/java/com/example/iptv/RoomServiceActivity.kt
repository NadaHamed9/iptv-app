package com.example.iptv

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

// Change from FragmentActivity() to BaseActivity()
class RoomServiceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_service)

        setupBackButton()

        // We use getString() so Android swaps the text based on the system language
        setupCategoryTile(R.id.btnBreakfast, getString(R.string.label_breakfast), R.drawable.ic_breakfast)
        setupCategoryTile(R.id.btnAllDayDining, getString(R.string.label_dining), R.drawable.ic_dining)
        setupCategoryTile(R.id.btnBeverages, getString(R.string.label_beverages), R.drawable.ic_beverages)
        setupCategoryTile(R.id.btnHousekeeping, getString(R.string.label_housekeeping), R.drawable.ic_housekeeping)
        setupCategoryTile(R.id.btnMyCart, getString(R.string.label_cart), R.drawable.ic_cart)
        setupCategoryTile(R.id.btnCallOrder, getString(R.string.label_call), R.drawable.ic_call)
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

    private fun setupCategoryTile(viewId: Int, label: String, iconRes: Int) {
        val view = findViewById<View>(viewId) ?: return

        val labelTextView = view.findViewById<TextView>(R.id.categoryLabel)
        val iconImageView = view.findViewById<ImageView>(R.id.categoryIcon)

        labelTextView.text = label
        iconImageView.setImageResource(iconRes)

        view.setOnClickListener {
            when (viewId) {
                R.id.btnCallOrder -> {
                    Toast.makeText(this, "Connecting to Room Service...", Toast.LENGTH_LONG).show()
                }
                R.id.btnMyCart -> {
                    Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Opening $label...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}