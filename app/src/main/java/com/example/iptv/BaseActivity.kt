package com.example.iptv

import android.content.Context
import androidx.fragment.app.FragmentActivity

open class BaseActivity : FragmentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Access shared preferences to find the saved language
        val prefs = newBase.getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_lang", "en") ?: "en"

        // Wrap the context using your LocaleHelper
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }
}