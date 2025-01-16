package com.prvavaja.grocerease

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.prvavaja.grocerease.databinding.ActivitySingleListBinding
import com.prvavaja.grocerease.lists.MyAdapterItems
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.GroceryList
import com.prvavaja.grocerease.model.Item
import com.prvavaja.grocerease.model.ItemInList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SingleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleListBinding
    lateinit var app: MyApplication
    lateinit var myAdapter: MyAdapterItems
    lateinit var storeName: String
    lateinit var listId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeName = intent.getStringExtra("STORE_NAME").toString()
        listId = intent.getStringExtra("LIST_ID").toString()
        app = application as MyApplication
        myAdapter = MyAdapterItems(app)

        binding.itemsRV.setHasFixedSize(true)
        binding.itemsRV.layoutManager = LinearLayoutManager(this)

        if (storeName != "null" && listId != "null") {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val today = LocalDateTime.now().format(formatter)
            val allLists = app.listOfgrocerylists.getAllLists()

            app.currentList = GroceryList(storeName, today, storeName, id = listId)

            for (list in allLists) {
                for (i in list.items) {
                    if (i.item.company == storeName) {
                        app.currentList.addItem(i)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val itemId = data?.getStringExtra("ITEM_ID")
            val itemName = data?.getStringExtra("ITEM_NAME")
            val itemDescription = data?.getStringExtra("ITEM_DESCRIPTION")
            val itemSubcategory = data?.getStringExtra("ITEM_SUBCATEGORY")
            val itemCompany = data?.getStringExtra("ITEM_COMPANY")
            val itemQuantity = data?.getStringExtra("ITEM_QUANTITY")

            if (itemName != null && itemDescription != null && itemSubcategory != null && itemCompany != null && itemQuantity != null) {
                val newItem = Item(
                    id = itemId,
                    name = itemName,
                    description = itemDescription,
                    subcategory = itemSubcategory,
                    company = itemCompany
                )

                val itemInList = ItemInList(item = newItem, quantity = itemQuantity)

                app.currentList.addItem(itemInList)
                myAdapter.notifyDataSetChanged()

                newItem.id?.let { itemId ->
                    listId.let { currentListId ->
                        BackendOperations().addItemToList(currentListId, itemId, itemQuantity) { success, error ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this, "${newItem.name} added to the database", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to add ${newItem.name} to the database: $error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun addOnClick(view: View) {
        val intent = Intent(this, AddEditItemActivity::class.java)
        intent.putExtra("LIST_ID", app.currentList.id)
        startActivityForResult(intent, 1)
    }

}