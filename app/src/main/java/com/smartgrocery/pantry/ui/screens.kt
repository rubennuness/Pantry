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
import com.smartgrocery.pantry.data.EanSearchProvider
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log
import androidx.compose.foundation.layout.heightIn
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode as MLBarcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.heightIn

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
        Text("Scan a barcode (EAN) or add sample items.")
        Spacer(Modifier.height(8.dp))
        BarcodeScanRow()
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { appAddSample(app) }) { Text("Add sample items") }
        }
    }
}

private fun appAddSample(app: AppState) {
    CoroutineScope(Dispatchers.IO).launch {
        app.addSampleDataIfEmpty()
    }
}

@Composable
private fun BarcodeScanRow(
    provider: ProductSearchProvider = when {
        BuildConfig.EAN_SEARCH_TOKEN.isNotBlank() -> EanSearchProvider()
        BuildConfig.SERPAPI_KEY.isNotBlank() -> SerpApiProvider(BuildConfig.SERPAPI_KEY)
        else -> MockProvider()
    }
) {
    var lastCode by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<StoreProduct>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val image = InputImage.fromFilePath(context, uri)
                    val scanner = BarcodeScanning.getClient(
                        com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(MLBarcode.FORMAT_EAN_8, MLBarcode.FORMAT_EAN_13)
                            .build()
                    )
                    val barcodes = scanner.process(image).await()
                    val code = barcodes.firstOrNull()?.rawValue.orEmpty()
                    Log.d("Barcode", "Detected=$code")
                    withContext(Dispatchers.Main) { lastCode = code }
                    if (code.isNotBlank()) {
                        val res = provider.search(code)
                        withContext(Dispatchers.Main) { results = res }
                    }
                } catch (t: Throwable) {
                    Log.e("Barcode", "Scan failed", t)
                }
            }
        }
    }

    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text("Pick image for barcode") }
            if (lastCode.isNotBlank()) Text("EAN: $lastCode")
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.heightIn(max = 300.dp)) {
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

@Suppress("UNCHECKED_CAST")
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> cont.resume(result) }
        addOnFailureListener { ex -> cont.resumeWithException(ex) }
        addOnCanceledListener { cont.cancel() }
    }

@Composable
private fun ProductSearchBox(
    provider: ProductSearchProvider = when {
        BuildConfig.EAN_SEARCH_TOKEN.isNotBlank() -> EanSearchProvider()
        BuildConfig.SERPAPI_KEY.isNotBlank() -> SerpApiProvider(BuildConfig.SERPAPI_KEY)
        else -> MockProvider()
    }
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<StoreProduct>>(emptyList()) }
    val scope = rememberCoroutineScope()
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                label = { Text("Search products") }
            )
            Button(onClick = {
                Log.d("ProductSearch", "Searching query='" + query + "' provider=" + provider.javaClass.simpleName)
                scope.launch(Dispatchers.IO) {
                    try {
                        val res = provider.search(query)
                        Log.d("ProductSearch", "Results size=" + res.size)
                        withContext(Dispatchers.Main) { results = res }
                    } catch (t: Throwable) {
                        Log.e("ProductSearch", "Search failed", t)
                        withContext(Dispatchers.Main) { results = emptyList() }
                    }
                }
            }) { Text("Search") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.heightIn(max = 300.dp)) {
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

