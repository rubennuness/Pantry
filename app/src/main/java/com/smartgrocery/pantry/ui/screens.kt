package com.smartgrocery.pantry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgrocery.pantry.data.PantryItem
import com.smartgrocery.pantry.data.wasteRisk

@Composable
fun InventoryList(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pantry", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(app.items) { item ->
                PantryItemCard(item)
            }
        }
    }
}

@Composable
fun PantryItemCard(item: PantryItem) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(listOfNotNull(item.category, item.unit?.let { "${item.quantity} $it" }).joinToString(" • "))
            item.expirationDate?.let { Text("Expires: $it (${item.wasteRisk()})") }
        }
    }
}

@Composable
fun ExpiringList(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Expiring soon", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(app.items.filter { it.expirationDate != null }.sortedBy { it.expirationDate }) { item ->
                PantryItemCard(item)
            }
        }
    }
}

@Composable
fun MealPlanList(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("5-day Meal Plan", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(app.meals) { meal ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(meal.title, style = MaterialTheme.typography.titleMedium)
                        meal.plannedDate?.let { Text(it) }
                        meal.description?.let { Text(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingList(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Shopping List", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("Dynamic by waste risk: replenish items not planned/used soon.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val candidates = app.items.filter { it.expirationDate == null || it.wasteRisk() == com.smartgrocery.pantry.data.WasteRisk.LOW }
            items(candidates) { item ->
                PantryItemCard(item)
            }
        }
    }
}

@Composable
fun ReceiptScanStub(app: AppState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Receipt Scan", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("MVP stub: tap to add sample item from a mock receipt.")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                app.addSampleDataIfEmpty()
            }) { Text("Add sample items") }
        }
    }
}

