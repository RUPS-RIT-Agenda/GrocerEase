package com.prvavaja.grocerease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.prvavaja.grocerease.databinding.ActivitySingleStoreBinding

class SingleStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeName = intent.getStringExtra("STORE_NAME")
        val storeAddress = intent.getStringExtra("STORE_ADDRESS")
        val storeImageUrl = intent.getStringExtra("STORE_IMAGE")
        val storePhoneNumber = intent.getStringExtra("STORE_NUMBER")

        binding.storeNameTextView.text = storeName
        binding.storeAddressTextView.text = storeAddress
        binding.phoneNumberTextView.text = storePhoneNumber

        Glide.with(this)
            .load(storeImageUrl)
            .into(binding.storeImageView)
    }

    fun backOnClick(view: View) {
        onBackPressed()
    }
}