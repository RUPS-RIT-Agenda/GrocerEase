package com.prvavaja.grocerease.model
import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
)

