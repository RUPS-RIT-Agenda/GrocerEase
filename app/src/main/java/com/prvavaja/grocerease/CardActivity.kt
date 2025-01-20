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
import com.prvavaja.grocerease.CameraActivity.Companion
import com.prvavaja.grocerease.databinding.ActivityCardBinding
import com.prvavaja.grocerease.databinding.ActivityHomeBinding
import com.prvavaja.grocerease.lists.CardAdapter
import com.prvavaja.grocerease.model.Card
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

@Serializable
data class CardModel(
    val _id: String,
    val __v: Int,
    val barcodeNum: String?,
    val shopName: String?,
    val cardImg: String?,
);

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding
    lateinit var app: MyApplication
    private lateinit var fabTakePhoto: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Load cards
        val client = OkHttpClient()

        val dotenv = dotenv {
            directory = "./assets"
            filename = "env" // instead of '.env', use 'env'
        }
        val apiHost = dotenv.get("API_HOST")
        val apiPort = dotenv.get("API_PORT")

        val request = Request.Builder()
            .url("http://$apiHost:$apiPort/api/card/get")
            .get()
            .build()

        val cardList = mutableListOf<Card>()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCards)
        recyclerView.layoutManager = LinearLayoutManager(this)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CardActivity", "Could not fetch: ${e.message}")
            }



            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val responseString = responseBody.string() // Fetch and store response body
                        Log.e("CardActivity", "SUCCESS fetch: $responseString")

                        try {
                            val cardModels = Json.decodeFromString<List<CardModel>>(responseString)

                            // Map CardModel objects to Card objects
                            val newCards = cardModels.map { v ->
                                Log.d("ImageTest","data:image/jpeg;base64," + (v.cardImg ?: ""));
                                Card(
                                    v.barcodeNum ?: "Test",
                                    v.shopName ?: "Test",
                                    "data:image/jpeg;base64," + (v.cardImg ?: "")
                                )
                            }

                            // Update the RecyclerView on the main thread
                            runOnUiThread {
                                cardList.clear()
                                cardList.addAll(newCards)
                                recyclerView.adapter = CardAdapter(cardList)
                            }
                        } catch (e: Exception) {
                            Log.e("CardActivity", "Parsing Error: ${e.message}")
                        }
                    }
                } else {
                    Log.e("CardActivity", "ERROR fetch: ${response.code} : ${response.message}")
                }
            }

        })
        //

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.fabTakePhoto.setOnClickListener {
            val cameraIntent = Intent(this,CameraActivity::class.java)
            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show()
            startActivity(cameraIntent)
        }

        // Example Card Data (Replace this with your OkHttp GET call)


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