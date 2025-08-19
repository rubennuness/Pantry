package com.smartgrocery.pantry.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val store: String?,
    val price: Double?,
    val url: String?,
    val ean: String?,
)

