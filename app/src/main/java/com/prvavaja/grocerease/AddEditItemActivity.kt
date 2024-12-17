package com.prvavaja.grocerease

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prvavaja.grocerease.databinding.ActivityAddEditItemBinding
import com.prvavaja.grocerease.model.Category
import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private val client = OkHttpClient()

    private var categories: List<Category> = emptyList()
    private val subcategoriesMap = mutableMapOf<String, List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.subcategoryMenu.visibility = View.GONE

        fetchCategoriesFromServer()
    }

    private fun fetchCategoriesFromServer() {
        val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
        }
        val apiHost = dotenv.get("API_HOST")
        val apiPort = dotenv.get("API_PORT")

        val request = Request.Builder()
            .url("http://$apiHost:$apiPort/api/category")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AddEditItemActivity", "Failed to fetch categories: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@AddEditItemActivity, "Error fetching categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonString = responseBody.string()
                        categories = parseCategoriesFromJson(jsonString)

                        categories.forEach { category ->
                            subcategoriesMap[category.name] = category.subcategories
                        }

                        runOnUiThread {
                            setupCategoryDropdown()
                        }
                    }
                } else {
                    Log.e("AddEditItemActivity", "Error response: ${response.message}")
                    runOnUiThread {
                        Toast.makeText(this@AddEditItemActivity, "Error fetching categories", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun parseCategoriesFromJson(jsonString: String): List<Category> {
        val categories = mutableListOf<Category>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val subcategoriesJsonArray = jsonObject.getJSONArray("subcategories")
                val subcategories = mutableListOf<String>()

                for (j in 0 until subcategoriesJsonArray.length()) {
                    subcategories.add(subcategoriesJsonArray.getString(j))
                }

                categories.add(Category(name, subcategories))
            }
        } catch (e: Exception) {
            Log.e("AddEditItemActivity", "Error parsing JSON: ${e.message}")
        }
        return categories
    }

    private fun setupCategoryDropdown() {
        val categoryNames = categories.map { it.name }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)

        val categoryDropdown = findViewById<AutoCompleteTextView>(R.id.categoryDropdown)
        categoryDropdown.setAdapter(categoryAdapter)

        categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            val subcategories = selectedCategory.subcategories

            if (subcategories.isNotEmpty()) {
                setupSubcategoryDropdown(subcategories)
                binding.subcategoryMenu.visibility = View.VISIBLE
            } else {
                binding.subcategoryMenu.visibility = View.GONE
                resetSubcategoryDropdown()
            }
        }
    }

    private fun setupSubcategoryDropdown(subcategories: List<String>) {
        val subcategoryDropdown = findViewById<AutoCompleteTextView>(R.id.subcategoryDropdown)
        val subcategoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subcategories)
        subcategoryDropdown.setAdapter(subcategoryAdapter)
        subcategoryDropdown.setText("")
    }

    private fun resetSubcategoryDropdown() {
        val subcategoryDropdown = findViewById<AutoCompleteTextView>(R.id.subcategoryDropdown)
        subcategoryDropdown.setText("")
        subcategoryDropdown.setAdapter(null)
    }
}
