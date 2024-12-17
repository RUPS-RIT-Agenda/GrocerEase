package com.prvavaja.grocerease

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.prvavaja.grocerease.databinding.ActivitySingleListBinding
import com.prvavaja.grocerease.lists.MyAdapterItems
import com.prvavaja.grocerease.model.GroceryList
import com.prvavaja.grocerease.model.Item
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SingleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleListBinding
    lateinit var app: MyApplication
    lateinit var myAdapter: MyAdapterItems
    lateinit var storeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeName = intent.getStringExtra("STORE_NAME").toString()
        app = application as MyApplication
        myAdapter = MyAdapterItems(app)

        binding.itemsRV.setHasFixedSize(true)
        binding.itemsRV.layoutManager = LinearLayoutManager(this)

        if (storeName != "null") {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val today = LocalDateTime.now().format(formatter)
            val allLists = app.listOfgrocerylists.getAllLists()

            app.currentList = GroceryList(storeName, today, storeName)

            for (list in allLists) {
                for (item in list.items) {
                    if (item.company == storeName) {
                        app.currentList.addItem(item)
                    }
                }
            }
        }

        binding.singleListTitleTV.text = app.currentList.listName
        binding.itemsRV.adapter = myAdapter

        binding.button.setOnClickListener { backOnClick(it) }
        binding.button2.setOnClickListener { addOnClick(it) }
    }


    fun backOnClick(view: View) {
        val intent = if (storeName != "null") {
            Intent(this, MapActivity::class.java)
        } else {
            Intent(this, ListsActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    fun addOnClick(view: View) {
        val intent = Intent(this, AddEditItemActivity::class.java)
        app.currentItem = Item("", "", "", "")
        app.currentList.addItem(app.currentItem)
        startActivity(intent)
    }
}