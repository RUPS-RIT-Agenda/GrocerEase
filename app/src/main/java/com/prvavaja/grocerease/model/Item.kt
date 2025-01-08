package com.prvavaja.grocerease.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import java.util.UUID

@Serializable
data class Item(
    @SerialName("name") var name: String,
    @SerialName("description") var description: String,
    @SerialName("subcategory") var subcategory: String,
    @SerialName("company") var company: String,
    @SerialName("_id") var id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherItem = other as Item
        return id == otherItem.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "item, Name: $name, UUID: $id, Company: $company, Description: $description, Subcategory: $subcategory"
    }
}
