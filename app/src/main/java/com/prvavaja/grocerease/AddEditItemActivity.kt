package com.prvavaja.grocerease

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.prvavaja.grocerease.databinding.ActivityAddEditItemBinding

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private lateinit var app: MyApplication
    private lateinit var myAdapter: MyAdapterLists
    private lateinit var serialization: Serialization

    private val stores = arrayOf(
        "None",
        "Mercator Market Pionirska Maribor",
        "Poslovni sistem Mercator d.d.",
        "Mercator Tržaška cesta",
        "Mercator Center",
        "Mercator Puhova ulica",
        "Lidl Koroška cesta",
        "Lidl Titova cesta",
        "Lidl Industrijska ulica",
        "Lidl Ulica I. Internacionale",
        "Lidl Tržaška cesta",
        "Lidl Ulica Veljka Vlahoviča",
        "Lidl Ptujska cesta",
        "Lidl Slivniška cesta",
        "Hofer Vodnikov trg",
        "Hofer Linhartova Ulica",
        "Hofer Slovenija",
        "Hofer Koroška cesta",
        "Hofer Ulica Veljka Vlahovića",
        "Hofer Šentiljska cesta",
        "Hofer Cesta proletarskih brigad",
        "Hofer Ptujska cesta",
        "Hofer Lenart",
        "Supermarket Spar Trg Svobode",
        "Supermarket Spar Žolgarjeva ulica",
        "InterSpar Pobreška cesta",
        "Hipermarket Spar Ulica Veljka Vlahoviča",
        "Restavracije InterSpar Pobreška cesta",
        "Supermarket Spar Prvomajska ulica",
        "Spar C. prolet. brigad",
        "Supermarket Spar Ptujska cesta"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        myAdapter = MyAdapterLists(app)
        serialization = Serialization(this)

        setupUI()

        populateFields()

        setupStoreDropdown()
    }

    private fun setupUI() {
        binding.itemTitleTV.text =
            if (app.currentItem.itemName.isEmpty()) "Add new item" else app.currentItem.itemName
    }

    private fun populateFields() {
        binding.itemNameET.editText?.setText(app.currentItem.itemName)
        binding.amountET.editText?.setText(app.currentItem.amount)
        binding.noteET.editText?.setText(app.currentItem.note)
    }

    private fun setupStoreDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stores)
        binding.storeDropdown.setAdapter(adapter)

        val selectedStoreIndex = stores.indexOf(app.currentItem.store)
        if (selectedStoreIndex >= 0) {
            binding.storeDropdown.setText(stores[selectedStoreIndex], false)
        }
    }

    fun deleteOnClick(view: View) {
        app.currentList.removeItem(app.currentItem.uuid)
        serialization.updateInfo(app.currentList.uuid, app.currentList)

        val intent = Intent(this, SingleListActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun saveOnClick(view: View) {
        app.currentItem.itemName = binding.itemNameET.editText?.text.toString()
        app.currentItem.amount = binding.amountET.editText?.text.toString()
        app.currentItem.note = binding.noteET.editText?.text.toString()
        app.currentItem.store = binding.storeDropdown.text.toString()

        serialization.updateInfo(app.currentList.uuid, app.currentList)

        val intent = Intent(this, SingleListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
