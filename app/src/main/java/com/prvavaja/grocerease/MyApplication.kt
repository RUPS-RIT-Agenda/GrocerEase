package com.prvavaja.grocerease

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.prvavaja.grocerease.model.GroceryList
import com.prvavaja.grocerease.model.Item
import com.prvavaja.grocerease.lists.ListOfGroceryLists
import com.prvavaja.grocerease.model.ItemInList
import com.prvavaja.grocerease.model.Serialization

class MyApplication : Application() {

    var listOfgrocerylists = ListOfGroceryLists()
    lateinit var currentList: GroceryList
    lateinit var currentItem: ItemInList
    var isGuest = false

    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DARK_MODE", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val serialization = Serialization(this)
        val info = serialization.readInfo()
        for (lists in info) {
            listOfgrocerylists.addList(lists)
        }
    }
}
