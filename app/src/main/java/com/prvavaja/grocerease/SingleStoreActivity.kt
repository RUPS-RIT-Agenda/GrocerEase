package com.prvavaja.grocerease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.prvavaja.grocerease.databinding.ActivitySingleStoreBinding
import com.prvavaja.grocerease.model.BackendOperations

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

    private fun addToFavorites(userId: String, storeId: String) {
        BackendOperations().addToFavorites(userId, storeId) { success, errorMessage ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Store added to favorites!", Toast.LENGTH_SHORT).show()
                    binding.imageButton.setImageResource(R.drawable.baseline_favorite_24) // Change to filled heart icon
                } else {
                    Toast.makeText(this, "Failed to add to favorites: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}