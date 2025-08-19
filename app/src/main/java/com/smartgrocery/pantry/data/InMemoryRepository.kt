package com.smartgrocery.pantry.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class InMemoryRepository {
    private val json = Json { prettyPrint = true }
    private val items = mutableListOf<PantryItem>()
    private val meals = mutableListOf<Meal>()

    fun getAllItems(): List<PantryItem> = items.sortedBy { it.expirationDate ?: "9999-12-31" }

    fun upsertItem(item: PantryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) items[index] = item else items += item
    }

    fun addItem(name: String, category: String?, quantity: Double, unit: String?, expirationDate: String?, purchasedDate: String?): PantryItem {
        val newItem = PantryItem(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            quantity = quantity,
            unit = unit,
            expirationDate = expirationDate,
            purchasedDate = purchasedDate
        )
        items += newItem
        return newItem
    }

    fun removeItem(id: String) { items.removeAll { it.id == id } }

    fun getMeals(): List<Meal> = meals
    fun upsertMeal(meal: Meal) {
        val index = meals.indexOfFirst { it.id == meal.id }
        if (index >= 0) meals[index] = meal else meals += meal
    }

    fun generateFiveDayPlan(todayIso: String): List<Meal> {
        val plan = mutableListOf<Meal>()
        val candidates = items.sortedBy { it.expirationDate ?: "9999-12-31" }
        for (i in 0 until 5) {
            val date = java.time.LocalDate.parse(todayIso).plusDays(i.toLong()).toString()
            val top = candidates.drop(i).firstOrNull()
            plan += Meal(
                id = UUID.randomUUID().toString(),
                title = top?.let { "Use ${it.name}" } ?: "Chef's Choice",
                description = top?.expirationDate?.let { "Expires $it" },
                plannedDate = date,
                usedItemIds = top?.let { listOf(it.id) } ?: emptyList()
            )
        }
        meals.clear(); meals.addAll(plan)
        return plan
    }

    fun exportState(): String = json.encodeToString(mapOf("items" to items, "meals" to meals))
}

