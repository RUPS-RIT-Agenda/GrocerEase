package com.prvavaja.grocerease

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding //ADD THIS LINE
    private lateinit var mapView: MapView
    private var currentMarkers = mutableListOf<Marker>()
    private var curentStore: String=""
    private var selectedStore: String=""
    data class MarkerData(val position: GeoPoint, val title: String)
    val markerCategories = mapOf(
        "Mercator" to listOf(
            MarkerData(GeoPoint(46.560608, 15.651450), "Mercator Market Pionirska Maribor"),
            MarkerData(GeoPoint(46.558662, 15.650357), "Poslovni sistem Mercator d.d."),
            MarkerData(GeoPoint(46.540264, 15.645581), "Mercator Tržaška cesta"),
            MarkerData(GeoPoint(46.539688, 15.640756), "Mercator Center"),
            MarkerData(GeoPoint(46.548233, 15.676722), "Mercator Puhova ulica")
        ),
        "Lidl" to listOf(
            MarkerData(GeoPoint(46.565464, 15.622417), "Lidl Koroška cesta"),
            MarkerData(GeoPoint(46.551441, 15.652140), "Lidl Titova cesta"),
            MarkerData(GeoPoint(46.560101, 15.659160), "Lidl Industrijska ulica "),
            MarkerData(GeoPoint(46.548622, 15.622083), "Lidl Ulica I. Internacionale"),
            MarkerData(GeoPoint(46.534573, 15.644635), "Lidl Tržaška cesta"),
            MarkerData(GeoPoint(46.547367, 15.675630), "Lidl Ulica Veljka Vlahoviča"),
            MarkerData(GeoPoint(46.526932, 15.673270), "Lidl Ptujska cesta"),
            MarkerData(GeoPoint(46.498730, 15.649705), "Lidl Slivniška cesta")
        ),
        "Hofer" to listOf(
            MarkerData(GeoPoint(46.559956, 15.650746), "Hofer Vodnikov trg"),
            MarkerData(GeoPoint(46.548660, 15.649803), "Hofer Linhartova Ulica"),
            MarkerData(GeoPoint(46.553188, 15.629921), "Hofer Slovenija"),
            MarkerData(GeoPoint(46.559097, 15.637163), "Hofer Koroška cesta"),
            MarkerData(GeoPoint(46.546593, 15.673613), "Hofer Ulica Veljka Vlahovića"),
            MarkerData(GeoPoint(46.583744, 15.666264), "Hofer Šentiljska cesta"),
            MarkerData(GeoPoint(46.545483, 15.617705), "Hofer Cesta proletarskih brigad"),
            MarkerData(GeoPoint(46.526493, 15.673833), "Hofer Ptujska cesta"),
            MarkerData(GeoPoint(46.580208, 15.837637), "Hofer Lenart")
        ),
        "Spar" to listOf(
            MarkerData(GeoPoint(46.560165, 15.648797), "Supermarket Spar Trg Svobode"),
            MarkerData(GeoPoint(46.549358, 15.642447), "Supermarket Spar Žolgarjeva ulica"),
            MarkerData(GeoPoint(46.554657, 15.653492), "InterSpar Pobreška cesta"),
            MarkerData(GeoPoint(46.551834, 15.677678), "Hipermarket Spar Ulica Veljka Vlahoviča"),
            MarkerData(GeoPoint(46.554291, 15.653976), "Restavracije InterSpar Pobreška cesta"),
            MarkerData(GeoPoint(46.532204, 15.668177), "Supermarket Spar Prvomajska ulica"),
            MarkerData(GeoPoint(46.546661, 15.619164), "Spar C. prolet. brigad"),
            MarkerData(GeoPoint(46.510986, 15.692998), "Supermarket Spar Ptujska cesta")

        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryDropdown = findViewById<AutoCompleteTextView>(R.id.categoryDropdown)
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            markerCategories.keys.toList()
        )
        categoryDropdown.setAdapter(categoryAdapter)

        categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = markerCategories.keys.toList()[position]
            curentStore = selectedCategory
            displayMarkers(selectedCategory)

            // Center map after selection
            val mapController = mapView.controller
            mapController.setZoom(14)
            val defaultLocation = GeoPoint(46.5547, 15.6459) // Adjust coordinates as needed
            mapController.setCenter(defaultLocation)
        }

        // Set up osmdroid configuration
        Configuration.getInstance().load(applicationContext, getPreferences(Context.MODE_PRIVATE))
        mapView = binding.map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(14)
        val defaultLocation = GeoPoint(46.5547, 15.6459)
        mapController.setCenter(defaultLocation)
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

    fun displayMarkers(category: String) {
        currentMarkers.forEach { it.closeInfoWindow() }
        mapView.overlays.clear()
        currentMarkers.clear()

        val selectedMarkers = markerCategories[category] ?: return
        for (markerData in selectedMarkers) {
            val marker = Marker(mapView)
            marker.position = markerData.position
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = markerData.title
            marker.icon = resources.getDrawable(R.drawable.marker_map_icon, null)
            marker.setOnMarkerClickListener { _, _ ->
                binding.selectedStoreTV.text = markerData.title
                selectedStore = curentStore
                true
            }
            mapView.overlays.add(marker)
            currentMarkers.add(marker)
        }
        mapView.invalidate()
    }
}