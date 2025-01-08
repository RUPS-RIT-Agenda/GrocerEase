package com.prvavaja.grocerease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.prvavaja.grocerease.databinding.ActivityMainBinding
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.Serialization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serialization: Serialization
    lateinit var app: MyApplication

    private val backendOperations = BackendOperations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            app.isGuest = false
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnGuest.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            app.isGuest=true  // Pass a flag to indicate guest status
            startActivity(intent)
        }
    }
}