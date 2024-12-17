package com.prvavaja.grocerease

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.prvavaja.grocerease.databinding.ActivityProfileBinding
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isInternetAvailable()) {
            loadUserDataFromServer()
        } else {
            loadUserDataFromSharedPrefs()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserDataFromSharedPrefs() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "N/A") ?: "N/A"
        val email = sharedPreferences.getString("email", "N/A") ?: "N/A"

        binding.usernameProfile.text = "Username: $username"
        binding.emailProfile.text = "Email: $email"

        val imageUriString = sharedPreferences.getString("profile_image", null)
        if (imageUriString != null) {
            loadImageFromUri(Uri.parse(imageUriString))
        } else {
            binding.userImage.setImageResource(R.drawable.profile)
        }
    }

    private fun loadUserDataFromServer() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://204.216.219.141:5000/api/user/profile")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileActivity", "Error fetching user data: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    loadUserDataFromSharedPrefs()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("ProfileActivity", "Response Code: ${response.code}")
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val responseData = responseBody.string()
                        Log.d("ProfileActivity", "Response Data: $responseData")

                        try {
                            val jsonResponse = JSONObject(responseData)
                            val username = jsonResponse.getString("username")
                            val email = jsonResponse.getString("email")
                            val profileImageUri = jsonResponse.getString("profile_image")

                            runOnUiThread {
                                binding.usernameProfile.text = "Username: $username"
                                binding.emailProfile.text = "Email: $email"
                                loadImageFromUri(Uri.parse(profileImageUri))
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileActivity", "Error parsing JSON response: ${e.message}")
                            runOnUiThread {
                                Toast.makeText(this@ProfileActivity, "Error parsing user data", Toast.LENGTH_SHORT).show()
                                loadUserDataFromSharedPrefs()  // Load from SharedPreferences as fallback
                            }
                        }
                    }
                } else {
                    Log.e("ProfileActivity", "Failed to fetch user data: ${response.message}")
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                        loadUserDataFromSharedPrefs()
                    }
                }
            }
        })
    }

    private fun loadImageFromUri(uri: Uri?) {
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                Glide.with(this)
                    .load(it)
                    .into(binding.userImage)

            } catch (e: SecurityException) {
                Log.e("ProfileActivity", "Security exception while accessing URI: ${e.message}")
                Toast.makeText(this, "Permission error. Could not load image.", Toast.LENGTH_SHORT).show()
                binding.userImage.setImageResource(R.drawable.profile) // Default image on error
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading image: ${e.message}")
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                binding.userImage.setImageResource(R.drawable.profile) // Default image on error
            }
        } ?: run {
            binding.userImage.setImageResource(R.drawable.profile)
        }
    }

    private fun logoutUser() {
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
