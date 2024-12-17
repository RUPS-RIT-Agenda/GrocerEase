package com.prvavaja.grocerease.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val name: String,
    val subcategories: List<String>
)
