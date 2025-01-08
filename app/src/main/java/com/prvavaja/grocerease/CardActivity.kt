package com.prvavaja.grocerease

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prvavaja.grocerease.databinding.ActivityCardBinding
import com.prvavaja.grocerease.databinding.ActivityHomeBinding
import com.prvavaja.grocerease.lists.CardAdapter
import com.prvavaja.grocerease.model.Card

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding
    lateinit var app: MyApplication
    private lateinit var fabTakePhoto: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCards)
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.fabTakePhoto.setOnClickListener {
            val cameraIntent = Intent(this,CameraActivity::class.java)
            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show()
            startActivity(cameraIntent)
        }

        // Example Card Data (Replace this with your OkHttp GET call)
        val cardList = listOf(
            Card("1234567890", "Shop A", "https://example.com/image1.jpg"),
            Card("9876543210", "Shop B", "https://example.com/image2.jpg")
        )

        val adapter = CardAdapter(cardList)
        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
        }
    }
}