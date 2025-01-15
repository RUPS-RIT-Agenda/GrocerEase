package com.prvavaja.grocerease

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.prvavaja.grocerease.databinding.ActivityFavouriteStoresBinding
class FavouriteStoresActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteStoresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteStoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeNames = listOf("Mercator", "Lidl", "Hofer")

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = StoreAdapter(storeNames)
    }

    fun backOnClick(view: View) {
        onBackPressed()
    }
}