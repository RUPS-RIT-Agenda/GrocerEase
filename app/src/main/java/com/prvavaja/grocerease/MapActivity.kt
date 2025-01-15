package com.prvavaja.grocerease

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.prvavaja.grocerease.databinding.ActivityMainBinding
import com.prvavaja.grocerease.databinding.ActivityMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var currentMarkers = mutableListOf<Marker>()
    private var curentStore: String = ""
    private var selectedStore: String = ""
    private val backendOperations = BackendOperations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryDropdown = findViewById<AutoCompleteTextView>(R.id.categoryDropdown)

        lifecycleScope.launch {
            val stores = backendOperations.fetchStores()
            val storeCategories = stores.groupBy { it.brand } // group stores by their brand

            val categoryAdapter = ArrayAdapter(
                this@MapActivity,
                android.R.layout.simple_dropdown_item_1line,
                storeCategories.keys.toList()
            )
            categoryDropdown.setAdapter(categoryAdapter)

            categoryDropdown.setOnItemClickListener { _, _, position, _ ->
                val selectedCategory = storeCategories.keys.toList()[position]
                curentStore = selectedCategory
                displayMarkers(storeCategories[selectedCategory] ?: emptyList())

                val mapController = mapView.controller
                mapController.setZoom(14)
                val defaultLocation = GeoPoint(46.5547, 15.6459)
                mapController.setCenter(defaultLocation)
            }
        }

        Configuration.getInstance().load(applicationContext, getPreferences(Context.MODE_PRIVATE))
        mapView = binding.map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(14)
        val defaultLocation = GeoPoint(46.5547, 15.6459)
        mapController.setCenter(defaultLocation)

        binding.favouritesButton.setOnClickListener {
            val intent = Intent(this@MapActivity, FavouriteStoresActivity::class.java)
            startActivity(intent)
        }
    }

    fun filterOnClick(view: View) {
        if (selectedStore.isNotEmpty()) {
            val intent = Intent(this, SingleListActivity::class.java)
            intent.putExtra("STORE_NAME", binding.selectedStoreTV.text.toString())
            intent.putExtra("STORE", selectedStore)
            startActivity(intent)
        } else {
            Toast.makeText(this, "You need to choose a store on the map.", Toast.LENGTH_SHORT).show()
        }
    }

    fun backOnClick(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun displayMarkers(stores: List<Store>) {
        currentMarkers.forEach { it.closeInfoWindow() }
        mapView.overlays.clear()
        currentMarkers.clear()

        for (store in stores) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(store.coordinates.lat, store.coordinates.lng)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = store.name
            marker.icon = resources.getDrawable(R.drawable.marker_map_icon, null)

            marker.setOnMarkerClickListener { _, _ ->
                val intent = Intent(this, SingleStoreActivity::class.java)
                intent.putExtra("STORE_NAME", store.name)
                intent.putExtra("STORE_ADDRESS", store.address)
                intent.putExtra("STORE_IMAGE", store.imageUrl)
                intent.putExtra("STORE_NUMBER", store.phoneNumber)
                startActivity(intent)
                true
            }
            mapView.overlays.add(marker)
            currentMarkers.add(marker)
        }
        mapView.invalidate()
    }

}
