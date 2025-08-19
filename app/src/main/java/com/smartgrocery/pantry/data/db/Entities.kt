package com.smartgrocery.pantry.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val quantity: Double,
    val unit: String?,
    val expirationDate: String?,
    val purchasedDate: String?,
    val parLevel: Double = 0.0,
    val avgDailyUse: Double = 0.0,
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val plannedDate: String?,
    val usedItemIdsJson: String, // JSON array of String ids
)

