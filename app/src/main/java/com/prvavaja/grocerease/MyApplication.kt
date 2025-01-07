package com.prvavaja.grocerease

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.prvavaja.grocerease.lists.ListOfGroceryLists
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.GroceryList
import com.prvavaja.grocerease.model.ItemInList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val backendOperations = BackendOperations()

        // Fetch lists using coroutines
        CoroutineScope(Dispatchers.Main).launch {
            val fetchedLists = backendOperations.fetchGroceryLists("677d9c1c0ba26c182a42f654")
            fetchedLists.forEach { listOfgrocerylists.addList(it) }
        }
    }
}
