package com.prvavaja.grocerease

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder

import java.util.UUID
@Serializable
class Item(var itemName:String,var store:String,var amaut:String) {
    @Serializable(with = UUIDSerializer::class)
    var uuid: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherItem = other as Item
        return uuid == otherItem.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "item, Name: "+itemName + "UUID: " + uuid +" Store: "+ store+" Amaunt"+amaut
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
}