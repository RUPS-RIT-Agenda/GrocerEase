package com.prvavaja.grocerease.model

import android.util.Log
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class BackendOperations {

    private val client = OkHttpClient()

    val dotenv = dotenv {
        directory = "./assets"
        filename = "env" // instead of '.env', use 'env'
    }

    val apiHost = dotenv.get("API_HOST")
    val apiPort = dotenv.get("API_PORT")

    private val baseUrl = "http://$apiHost:$apiPort/api/list"

    suspend fun fetchGroceryLists(userId: String): List<GroceryList> {
        val url = "$baseUrl/usersLists/$userId"
        val request = Request.Builder().url(url).get().build()

        val json = Json { ignoreUnknownKeys = true }

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val jsonObject = json.parseToJsonElement(it).jsonObject
                        val listsJson = jsonObject["lists"]!!
                        json.decodeFromJsonElement<List<GroceryList>>(listsJson)
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: IOException) {
                Log.e("BackendOperations", "Error fetching lists", e)
                emptyList()
            }
        }
    }

    fun createList(
        userID: String,
        name: String,
        company: String,
        description: String,
        date: String,
        callback: (GroceryList?) -> Unit
    ) {
        val url = "$baseUrl/create-empty-list"
        val requestBody = """
            {
                "userID": "$userID",
                "name": "$name",
                "company": "$company",
                "description": "$description",
                "date": "$date"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val createdList = Json.decodeFromString<GroceryList>(it)
                        callback(createdList)
                    } ?: callback(null)
                } else {
                    Log.e("BackendOperations", "Failed to create list: ${response.message}")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e("BackendOperations", "Error creating list on backend", e)
                callback(null)
            }
        }.start()
    }

    fun addItemToList(
        listId: String,
        itemId: String,
        quantity: String,
        note: String? = null,
        bought: Boolean = false,
        callback: (Boolean, String?) -> Unit
    ) {
        val url = "$baseUrl/$listId/add-item"
        val requestBody = """
        {
            "itemId": "$itemId",
            "quantity": "$quantity",
            "note": ${if (note != null) "\"$note\"" else "null"},
            "bought": $bought
        }
    """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, response.message)
                }
            } catch (e: Exception) {
                Log.e("BackendOperations", "Error adding item to list", e)
                callback(false, e.message)
            }
        }.start()
    }

}
