package com.prvavaja.grocerease

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.view.View
import com.prvavaja.grocerease.databinding.SettingsLayoutBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsLayoutBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)

        val isDarkMode = sharedPref.getBoolean("DARK_MODE", false)
        binding.switchTheme.isChecked = isDarkMode

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            toggleDarkMode(isChecked)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Notifications are " + (if (isChecked) "on" else "off"), Toast.LENGTH_SHORT).show()
        }

        binding.systemInfoText.text = getSystemInfo()

        binding.backBTN.setOnClickListener {
            backOnClick()
        }
    }

    private fun toggleDarkMode(isEnabled: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean("DARK_MODE", isEnabled)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun getSystemInfo(): String {
        val apiLevel = android.os.Build.VERSION.SDK_INT.toString()
        val device = android.os.Build.DEVICE ?: "N/A"
        val model = android.os.Build.MODEL ?: "N/A"
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName

        return "API Level: $apiLevel\nDevice: $device\nModel: $model\nApp Version: $versionName"
    }

    private fun backOnClick() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}