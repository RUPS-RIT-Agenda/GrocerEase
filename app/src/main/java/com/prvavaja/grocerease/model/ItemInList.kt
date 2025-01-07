package com.prvavaja.grocerease.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ItemInList(
    @SerialName("itemId") var item: Item,
    @SerialName("bought") var bought: Boolean = false,
    @SerialName("note") var note: String? = null,
    @SerialName("quantity") var quantity: String = "1",
    @SerialName("_id") var id: String? = null
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
