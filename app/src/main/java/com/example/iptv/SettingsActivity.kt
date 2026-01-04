package com.example.iptv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.FragmentActivity

class SettingsActivity : BaseActivity() {

    /**
     * This ensures the Settings screen itself stays in the correct language
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_lang", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editName = findViewById<EditText>(R.id.editGuestName)
        val spinner = findViewById<Spinner>(R.id.languageSpinner)
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)

        // 1. Setup Language Spinner
        val languages = arrayOf("English", "Arabic", "French", "Spanish")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 2. Load Saved Data from "HotelPrefs"
        val prefs = getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)

        // Load Guest Name
        editName.setText(prefs.getString("guest_name", "John Tyler"))

        // Set Spinner position based on saved language code
        val savedLangCode = prefs.getString("app_lang", "en")
        val savedLangPos = when(savedLangCode) {
            "ar" -> 1
            "fr" -> 2
            "es" -> 3
            else -> 0 // Default to English
        }
        spinner.setSelection(savedLangPos)

        // 3. Save Button Logic
        btnSave.setOnClickListener {
            val selectedUI = spinner.selectedItem.toString()
            val newName = editName.text.toString()

            // MAP the UI name to the Folder code (values-ar, values-fr, etc.)
            val langCode = when (selectedUI) {
                "Arabic" -> "ar"
                "French" -> "fr"
                "Spanish" -> "es"
                else -> "en"
            }

            prefs.edit().apply {
                putString("guest_name", newName)
                putString("app_lang", langCode)
                apply()
            }

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()

            // RESTART THE APP: This is the only way to apply the language change
            // throughout the entire application.
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}