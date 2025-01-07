package com.prvavaja.grocerease.model

import kotlinx.serialization.Serializable

@Serializable
class GroceryList(
    var listName: String,
    var date: String,
    var company: String,
    var items: MutableList<ItemInList> = mutableListOf(), // Use ItemInList to match backend
    var id: String? = null // ID from the backend
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherGroceryList = other as GroceryList
        return id == otherGroceryList.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Grocery list, name: $listName, ID: $id, Store: $company, Items: $items"
    }

    fun addItem(item: ItemInList) {
        items.add(item)
    }

    fun getItem(itemId: String): ItemInList? {
        return items.find { it.id == itemId }
    }

    fun updateItem(itemId: String, updatedItem: ItemInList) {
        val index = items.indexOfFirst { it.id == itemId }
        if (index != -1) {
            items[index] = updatedItem
        }
    }

    fun removeItem(itemId: String) {
        items.removeIf { it.id == itemId }
    }

    fun getAllItems(): List<ItemInList> {
        return items
    }
}