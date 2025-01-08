package com.prvavaja.grocerease

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prvavaja.grocerease.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

        if (app.isGuest) {
            binding.btnProfile.visibility = View.GONE
        } else {
            binding.btnProfile.visibility = View.VISIBLE
        }

        binding.listButton.setOnClickListener {
            val intent = Intent(this, ListsActivity::class.java)
            startActivity(intent)
        }

        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnCards.setOnClickListener {
            val intent = Intent(this, CardActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
