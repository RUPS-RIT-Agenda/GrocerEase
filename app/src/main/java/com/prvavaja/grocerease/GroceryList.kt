package com.prvavaja.grocerease

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder

import java.util.UUID
@Serializable
class GroceryList(var lisrName:String,var date:String,var items: MutableList<Item> = mutableListOf()) {
    @Serializable(with = UUIDSerializer::class)
    var uuid: UUID = UUID.randomUUID()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherGroceryList = other as GroceryList
        return uuid == otherGroceryList.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "Grocery list, name: " + lisrName+ "UUID:" +uuid +" Items: "+ items.toString()
    }
    object UUIDSerializer : KSerializer<UUID> {
        override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }

        override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }


    }
///delo z listom itemov
    fun addItem(item: Item) {
        items.add(item)
    }

    fun getItem(uuid: UUID): Item? {
        return items.find { it.uuid == uuid }
    }

    fun updateItem( uuid: UUID,updatedItem: Item) {
        val index = items.indexOfFirst { it.uuid == uuid }
        if (index != -1) {
            items[index] = updatedItem
        }
    }
    fun removeItem(item: Item) {
        items.remove(item)
    }
    fun getAllItems(): List<Item> {
        val sortedItems = items
        for (item in sortedItems) {
            println(item.toString())
        }
        return sortedItems
    }
    fun velikost(): Int {
        return items.size
    }
    fun getLastItem(): Item {
        return items.last()
    }


}