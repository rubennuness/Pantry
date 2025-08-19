package com.smartgrocery.pantry.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.smartgrocery.pantry.data.Meal
import com.smartgrocery.pantry.data.PantryItem
import com.smartgrocery.pantry.data.Repository
import com.smartgrocery.pantry.data.RoomRepository
import com.smartgrocery.pantry.data.ShoppingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.UUID

class AppState(
    private val repository: Repository
) {
    var items by mutableStateOf(listOf<PantryItem>())
    var meals by mutableStateOf(listOf<Meal>())
    var shopping by mutableStateOf(listOf<ShoppingItem>())

    fun startCollecting() {
        // No-op here; see LaunchedEffect in rememberAppState
    }

    suspend fun addSampleDataIfEmpty() {
        if (items.isNotEmpty()) return
        val today = java.time.LocalDate.now()
        withContext(Dispatchers.IO) {
            repository.upsertItem(
                PantryItem(UUID.randomUUID().toString(), "Milk", "Dairy", 1.0, "L", today.plusDays(2).toString(), today.minusDays(1).toString())
            )
            repository.upsertItem(
                PantryItem(UUID.randomUUID().toString(), "Chicken", "Meat", 1.0, "kg", today.plusDays(1).toString(), today.toString())
            )
            repository.upsertItem(
                PantryItem(UUID.randomUUID().toString(), "Lettuce", "Produce", 2.0, null, today.plusDays(3).toString(), today.toString())
            )
            repository.generateFiveDayPlan(today.toString())
        }
    }
}

@Composable
fun rememberAppState(): AppState {
    val context = LocalContext.current.applicationContext as Application
    val repo = remember { RoomRepository.get(context) }
    val state = remember { AppState(repo) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { state.addSampleDataIfEmpty() }
        repo.itemsFlow.collectLatest { state.items = it }
    }
    LaunchedEffect(Unit) {
        repo.mealsFlow.collectLatest { state.meals = it }
    }
    LaunchedEffect(Unit) {
        repo.shoppingFlow.collectLatest { state.shopping = it }
    }
    return state
}

