package com.smartgrocery.pantry.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import com.smartgrocery.pantry.data.InMemoryRepository
import com.smartgrocery.pantry.data.Meal
import com.smartgrocery.pantry.data.PantryItem

class AppState(
    private val repository: InMemoryRepository
) {
    var items by mutableStateOf(listOf<PantryItem>())
        private set
    var meals by mutableStateOf(listOf<Meal>())
        private set

    fun refresh() {
        items = repository.getAllItems()
        meals = repository.getMeals()
    }

    fun addSampleDataIfEmpty() {
        if (items.isNotEmpty()) return
        val today = java.time.LocalDate.now()
        repository.addItem("Milk", "Dairy", 1.0, "L", today.plusDays(2).toString(), today.minusDays(1).toString())
        repository.addItem("Chicken", "Meat", 1.0, "kg", today.plusDays(1).toString(), today.toString())
        repository.addItem("Lettuce", "Produce", 2.0, null, today.plusDays(3).toString(), today.toString())
        refresh()
        repository.generateFiveDayPlan(today.toString())
        refresh()
    }
}

@Composable
fun rememberAppState(): AppState {
    val repo = remember { InMemoryRepository() }
    val state = remember { AppState(repo) }
    state.addSampleDataIfEmpty()
    return state
}

