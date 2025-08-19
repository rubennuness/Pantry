package com.smartgrocery.pantry.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PantryItem(
    val id: String,
    val name: String,
    val category: String?,
    val quantity: Double,
    val unit: String?,
    val expirationDate: String?, // ISO_LOCAL_DATE for simplicity in MVP
    val purchasedDate: String?,
)

@Serializable
data class Meal(
    val id: String,
    val title: String,
    val description: String?,
    val plannedDate: String?,
    val usedItemIds: List<String> = emptyList(),
)

enum class WasteRisk { LOW, MEDIUM, HIGH }

fun PantryItem.wasteRisk(todayIso: String = LocalDate.now().toString()): WasteRisk {
    val exp = expirationDate ?: return WasteRisk.LOW
    return try {
        val days = java.time.Period.between(LocalDate.parse(todayIso), LocalDate.parse(exp)).days
        when {
            days <= 2 -> WasteRisk.HIGH
            days <= 5 -> WasteRisk.MEDIUM
            else -> WasteRisk.LOW
        }
    } catch (_: Throwable) { WasteRisk.LOW }
}

data class ShoppingItem(
    val id: String = "",
    val title: String,
    val store: String?,
    val price: Double?,
    val url: String?,
    val ean: String?,
)

