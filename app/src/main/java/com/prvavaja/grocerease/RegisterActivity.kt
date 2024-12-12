package com.prvavaja.grocerease

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.prvavaja.grocerease.databinding.ActivityRegisterBinding
import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var imageUri: Uri? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginLink.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                binding.userImage.setImageURI(imageUri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.userImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser() {
        val username = binding.usernameRegister.editText?.text.toString().trim()
        val email = binding.emailRegister.editText?.text.toString().trim()
        val password = binding.passwordRegister.editText?.text.toString().trim()
        val confirmPassword = binding.confirmPasswordRegister.editText?.text.toString().trim()

        // Validate input fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Log.e("RegisterActivity", "Please fill in all fields")
            return
        }

        if (!isValidEmail(email)) {
            Log.e("RegisterActivity", "Invalid email format")
            binding.emailRegister.error = "Invalid email format"
            return
        }

        if (password != confirmPassword) {
            Log.e("RegisterActivity", "Passwords do not match")
            return
        }

        if (password.length < 5) {
            Log.e("RegisterActivity", "Password must be at least 5 characters")
            binding.passwordRegister.error = "Password must be at least 5 characters"
            return
        }

        val hashedPassword = hashPassword(password)

        // Save data to SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("password", hashedPassword)
        editor.putString("profile_image", imageUri.toString())
        editor.apply()

        // Create HTTP request
        val client = OkHttpClient()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonBody = """
            {
                "username": "$username",
                "email": "$email",
                "password": "$hashedPassword",
                "profile_image": "${imageUri.toString()}"
            }
        """.trimIndent()

        val dotenv = dotenv {
            directory = "./assets"
            filename = "env" // instead of '.env', use 'env'
        }
        val apiHost = dotenv.get("API_HOST")
        val apiPort = dotenv.get("API_PORT")

        val requestBody = jsonBody.toRequestBody(jsonMediaType)
        Log.d("RegisterActivity", jsonBody)
        val request = Request.Builder()
            .url("http://$apiHost:$apiPort/api/user/register")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RegisterActivity", "Registration failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("RegisterActivity", "Registration successful!")
                    runOnUiThread {
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("RegisterActivity", "Registration failed: ${response.message}")
                }
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
        return email.matches(Regex(emailPattern))
    }

    private fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { String.format("%02x", it) }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            password
        }
    }
}
