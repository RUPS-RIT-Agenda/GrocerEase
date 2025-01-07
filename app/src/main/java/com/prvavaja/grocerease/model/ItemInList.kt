package com.prvavaja.grocerease.model

import kotlinx.serialization.Serializable

@Serializable
class ItemInList(
    var item: Item,
    var bought: Boolean = false,
    var note: String? = null,
    var quantity: String = "1",
    var id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherItemInList = other as ItemInList
        return id == otherItemInList.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "ItemInList(item=$item, bought=$bought, note=$note, quantity=$quantity, id=$id)"
    }
}
