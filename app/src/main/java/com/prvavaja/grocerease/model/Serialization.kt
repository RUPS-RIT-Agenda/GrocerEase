package com.prvavaja.grocerease.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import android.content.Context

class Serialization(private val context: Context) {
    val FILE_JSON = "grocereaseinfo.json"

    fun saveInfo(info: List<GroceryList>) {
        val jsonString = Json.encodeToString(info)
        File(context.filesDir, FILE_JSON).writeText(jsonString)
    }

    fun readInfo(): List<GroceryList> {
        return try {
            val file = File(context.filesDir, FILE_JSON)
            val jsonString = file.readText()
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addInfo(newList: GroceryList) {
        val curentInfo = readInfo().toMutableList()
        println("uuid add")
        println(newList)
        curentInfo.add(newList)
        saveInfo(curentInfo)
    }

    fun updateInfo(id: String?, updatedList: GroceryList) {
        if (id != null) {
            val curentInfo = readInfo().toMutableList()
            val index = curentInfo.indexOfFirst { it.id == id }

            if (index != -1) {
                println("id update")
                println(updatedList)
                curentInfo[index] = updatedList
                saveInfo(curentInfo)
            }
        }
    }

    fun delete(id: String?) {
        if (id != null) {
            val curentInfo = readInfo().toMutableList()
            println("uuid delete")
            println(id)
            val newInfo = curentInfo.filter { it.id != id }
            saveInfo(newInfo)
        }
    }

    fun deleteAllInfo() {
        File(context.filesDir, FILE_JSON).delete()
    }
}