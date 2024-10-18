package com.prvavaja.grocerease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.prvavaja.grocerease.databinding.ActivityMainBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding //ADD THIS LINE
    private lateinit var serialization: Serialization
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater) //ADD THIS LINE

        val serialization=Serialization(this)
        var seznametest=ListOfGroceryLists()
        setContentView(binding.root)
        var item1=Item("voda","mercator","4 l")
        var item2=Item("sok","tus","5 l")
        var item3=Item("jagode","spar","250 g")
         val items = mutableListOf<Item>()
        var list1=GroceryList("prvi list","20.12.2024", items)
        list1.addItem(item1)
        list1.addItem(item2)
        list1.addItem(item3)
        println(list1.toString())
        val jsonString = Json.encodeToString(list1)
        println(jsonString)
        list1.getAllItems()
        seznametest.addList(list1)
        serialization.addInfo(list1)
        println("red from json")
        val podatki=serialization.readInfo()
        for (sadje in podatki) {
            println(sadje)
            //list.addItem(sadje)
        }
        var itemupdate=Item("voda","jysk","4 l")

        list1.updateItem(item1.uuid,itemupdate)
        serialization.updateInfo(list1.uuid,list1)
        val podatki2=serialization.readInfo()
        println("////")
        for (sadje2 in podatki2) {
            println(sadje2)
            //list.addItem(sadje)
        }
        //serialization.deleteAllInfo()
        println("/////////")
        val podatki23=serialization.readInfo()
        for (sadje3 in podatki23) {
            println(sadje3)
            //list.addItem(sadje)
        }






        //setContentView(R.layout.activity_main)
    }
}