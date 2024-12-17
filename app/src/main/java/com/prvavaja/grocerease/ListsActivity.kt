package com.prvavaja.grocerease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.databinding.ActivityListsBinding
import com.prvavaja.grocerease.model.GroceryList
import com.prvavaja.grocerease.model.MyAdapterLists
import com.prvavaja.grocerease.model.Serialization
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListsBinding
    lateinit var app: MyApplication
    lateinit var myAdapter: MyAdapterLists
    lateinit var serialization: Serialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serialization = Serialization(this)
        app = application as MyApplication
        myAdapter = MyAdapterLists(app)

        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ListsActivity)
            adapter = myAdapter
        }

        binding.backBTN.setOnClickListener { backOnClick() }
        binding.btnAdd.setOnClickListener { addListOnClick() }
    }


    private fun backOnClick() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addListOnClick() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
        val addListNameET = dialogLayout.findViewById<EditText>(R.id.addListNameET)
        val companySpinner = dialogLayout.findViewById<Spinner>(R.id.companySpinner)
        val recyclerView: RecyclerView = this.findViewById(R.id.recyclerView)

        val companyAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        companySpinner.adapter = companyAdapter

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val today = LocalDateTime.now().format(formatter)

        val confirmBuilder = AlertDialog.Builder(this)

        with(builder) {
            setTitle("Name the new list:")
            setPositiveButton("OK") { _, _ ->
                val listName = addListNameET.text.toString().trim()
                val selectedCompany = companySpinner.selectedItem.toString()

                if (listName.isNotEmpty()) {
                    confirmBuilder.setTitle("Confirm")
                        .setMessage("Are you sure you want to create a new Shopping list named \"$listName\" for company \"$selectedCompany\"?")
                        .setPositiveButton("Yes") { _, _ ->
                            app.listOfgrocerylists.addList(GroceryList(listName, today, selectedCompany))
                            recyclerView.adapter?.notifyItemInserted(app.listOfgrocerylists.size() - 1)
                            serialization.addInfo(app.listOfgrocerylists.getLastList())
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            Log.d("Lists", "List creation canceled!")
                        }
                    confirmBuilder.create().show()
                } else {
                    Log.d("Lists", "List name is empty, not creating!")
                }
            }
            setNegativeButton("Cancel") { _, _ ->
                Log.d("Lists", "Adding a list canceled!")
            }
            setView(dialogLayout)
            show()
        }
    }

}