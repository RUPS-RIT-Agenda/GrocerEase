package com.prvavaja.grocerease.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    @SerialName("_id") val id: String,
    val brand: String,
    val name: String,
    val imageUrl: String,
    val address: String,
    val phoneNumber: String,
    val coordinates: Coordinates,
    val __v: Int
)
