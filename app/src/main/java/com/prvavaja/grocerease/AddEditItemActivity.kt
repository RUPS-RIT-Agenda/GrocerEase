package com.prvavaja.grocerease

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.prvavaja.grocerease.databinding.ActivityAddEditItemBinding
import com.prvavaja.grocerease.lists.ItemsAdapter
import com.prvavaja.grocerease.lists.ItemsInListAdapter
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.Category
import com.prvavaja.grocerease.model.Item
import com.prvavaja.grocerease.model.ItemInList
import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private val client = OkHttpClient()
    private var categories: List<Category> = emptyList()
    private val subcategoriesMap = mutableMapOf<String, List<String>>()
    private val itemsList = mutableListOf<Item>()
    private val itemsInList = mutableListOf<ItemInList>()
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemsInListAdapter: ItemsInListAdapter
    private var isSearching: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        binding.subcategoryMenu.visibility = View.GONE
        setupRecyclerViews()
        fetchCategoriesFromServer()
    }

    private fun setupRecyclerViews() {
        itemsAdapter = ItemsAdapter(itemsList) { item ->
            showAddItemDialog(item)
        }

        itemsInListAdapter = ItemsInListAdapter(itemsInList) {  }

        binding.itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemsRecyclerView.adapter = itemsInListAdapter
    }

    private fun toggleAdapter() {
        binding.itemsRecyclerView.adapter = if (isSearching) itemsAdapter else itemsInListAdapter
    }

    private fun fetchCategoriesFromServer() {
        val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
        }
        val apiHost = dotenv["API_HOST"]
        val apiPort = dotenv["API_PORT"]

        val request = Request.Builder()
            .url("http://$apiHost:$apiPort/api/category")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AddEditItemActivity", "Failed to fetch categories: ${e.message}")
                runOnUiThread { Toast.makeText(this@AddEditItemActivity, "Error fetching categories", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonString = responseBody.string()
                        categories = parseCategoriesFromJson(jsonString)
                        categories.forEach { subcategoriesMap[it.name] = it.subcategories }
                        runOnUiThread { setupCategoryDropdown() }
                    }
                }
            }
        })
    }

    private fun parseCategoriesFromJson(jsonString: String): List<Category> {
        val categories = mutableListOf<Category>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val subcategories = List(jsonObject.getJSONArray("subcategories").length()) {
                jsonObject.getJSONArray("subcategories").getString(it)
            }
            categories.add(Category(name, subcategories))
        }
        return categories
    }

    private fun setupCategoryDropdown() {
        val categoryNames = categories.map { it.name }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
        binding.categoryDropdown.setAdapter(categoryAdapter)

        binding.categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]

            binding.subcategoryDropdown.text.clear()
            binding.subcategoryMenu.visibility = View.GONE

            itemsList.clear()
            itemsAdapter.notifyDataSetChanged()

            setupSubcategoryDropdown(selectedCategory.subcategories)
        }
    }

    private fun showAddItemDialog(item: Item) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val quantityInput = dialogView.findViewById<EditText>(R.id.quantityEditText)

        AlertDialog.Builder(this)
            .setTitle("Add Item Quantity")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val quantity = quantityInput.text.toString()
                if (quantity.isNotEmpty()) {
                    val listId = "677d855444e36ca6fa5a2370" // Replace with actual list ID
                    item.id?.let { itemId ->
                        BackendOperations().addItemToList(listId, itemId, quantity) { success, error ->
                            runOnUiThread {
                                if (success) {
                                    val itemInList = ItemInList(
                                        item = item,
                                        quantity = quantity
                                    )
                                    // Add the item to itemsInList
                                    itemsInList.add(itemInList)
                                    itemsInListAdapter.notifyDataSetChanged()
                                    toggleAdapter() // Switch back to added items view
                                    Toast.makeText(this, "${item.name} added with quantity: $quantity", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to add item: $error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupSubcategoryDropdown(subcategories: List<String>) {
        val subcategoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subcategories)
        binding.subcategoryDropdown.setAdapter(subcategoryAdapter)

        binding.subcategoryDropdown.setOnItemClickListener { _, _, _, _ ->
            fetchItemsBySubcategory(binding.subcategoryDropdown.text.toString())
        }
        binding.subcategoryMenu.visibility = View.VISIBLE
    }

    private fun fetchItemsBySubcategory(subcategory: String) {
        isSearching = true
        toggleAdapter()

        val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
        }
        val apiHost = dotenv["API_HOST"]
        val apiPort = dotenv["API_PORT"]

        val url = "http://$apiHost:$apiPort/api/item/$subcategory"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AddEditItemActivity", "Request failed: ${e.message}")
                runOnUiThread {
                    isSearching = false
                    toggleAdapter()
                    Toast.makeText(this@AddEditItemActivity, "Failed to fetch items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonString = responseBody.string()

                        val items = mutableListOf<Item>()
                        try {
                            val jsonArray = JSONArray(jsonString)
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val name = jsonObject.getString("name")
                                val description = jsonObject.getString("description")
                                val subcategory = jsonObject.getString("subcategory")
                                val company = jsonObject.getString("company")
                                items.add(Item(name, description, subcategory, company))
                            }
                            runOnUiThread {
                                itemsList.clear()
                                itemsList.addAll(items)
                                itemsAdapter.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            Log.e("AddEditItemActivity", "Error parsing items: ${e.message}")
                        }
                    }
                } else {
                    runOnUiThread {
                        isSearching = false
                        toggleAdapter()
                        Toast.makeText(this@AddEditItemActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
