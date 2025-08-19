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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgrocery.pantry.data.PantryItem
import com.smartgrocery.pantry.data.wasteRisk
import com.smartgrocery.pantry.BuildConfig
import com.smartgrocery.pantry.data.MockProvider
import com.smartgrocery.pantry.data.ProductSearchProvider
import com.smartgrocery.pantry.data.StoreProduct
import com.smartgrocery.pantry.data.SerpApiProvider
import kotlinx.coroutines.withContext
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        Spacer(Modifier.height(8.dp))
        ProductSearchBox()
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
        Text("Add from receipt image using OCR (simple parser).")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { appAddSample(app) }) { Text("Add sample items") }
            // In a full implementation: launch photo picker, run ML Kit OCR, parse lines to items
        }
    }
}

private fun appAddSample(app: AppState) {
    CoroutineScope(Dispatchers.IO).launch {
        app.addSampleDataIfEmpty()
    }
}

@Composable
private fun ProductSearchBox(provider: ProductSearchProvider = SerpApiProvider(BuildConfig.SERPAPI_KEY).let { p ->
    if (BuildConfig.SERPAPI_KEY.isBlank()) MockProvider() else p
}) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<StoreProduct>>(emptyList()) }
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                label = { Text("Search products") }
            )
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val res = provider.search(query)
                    withContext(Dispatchers.Main) { results = res }
                }
            }) { Text("Search") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(results) { p ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${p.store}: ${p.title}")
                        p.price?.let { Text("€$it") }
                        Text(p.url, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

