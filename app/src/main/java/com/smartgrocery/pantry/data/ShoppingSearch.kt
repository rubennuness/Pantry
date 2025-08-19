package com.smartgrocery.pantry.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import com.smartgrocery.pantry.BuildConfig
import java.text.Normalizer

data class StoreProduct(
    val store: String,
    val title: String,
    val price: Double?,
    val url: String,
)

interface ProductSearchProvider {
    suspend fun search(query: String): List<StoreProduct>
}

class EanSearchProvider(
    private val token: String = BuildConfig.EAN_SEARCH_TOKEN,
    private val baseUrl: String = BuildConfig.EAN_SEARCH_BASE_URL,
    private val client: OkHttpClient = OkHttpClient()
) : ProductSearchProvider {
    override suspend fun search(query: String): List<StoreProduct> {
        if (token.isBlank()) return emptyList()

        // If query looks like a barcode, try barcode endpoint first
        val normalized = query.filter { it.isDigit() }
        if (normalized.length == 8 || normalized.length == 13) {
            buildBarcodeUrl(normalized).also { Log.d("EanSearch", it) }
            request(buildBarcodeUrl(normalized))?.let { return it }
        }

        // Try progressively simplified queries
        val attempts = buildQueryAttempts(query)
        for (q in attempts) {
            val url = buildSearchUrl(q)
            Log.d("EanSearch", url)
            request(url)?.takeIf { it.isNotEmpty() }?.let { return it }
        }
        return emptyList()
    }

    private fun buildBarcodeUrl(ean: String): String =
        "$baseUrl?op=barcode-lookup&format=json&token=$token&ean=$ean"

    private fun buildSearchUrl(term: String): String =
        "$baseUrl?op=search&format=json&token=$token&term=" + java.net.URLEncoder.encode(term, "UTF-8")

    private fun request(url: String): List<StoreProduct>? {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            val code = resp.code
            val bodyStr = resp.body?.string() ?: return null
            Log.d("EanSearch", "HTTP $code, ${'$'}{bodyStr.length} bytes")
            val list = mutableListOf<StoreProduct>()
            val trimmed = bodyStr.trim()
            if (trimmed.startsWith("[")) {
                // Array response (e.g., barcode-lookup)
                val arr = org.json.JSONArray(trimmed)
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val title = o.optString("name", o.optString("title", ""))
                    val codeStr = o.optString("ean", o.optString("gtin", ""))
                    val urlItem = o.optString("url", if (codeStr.isNotBlank()) "https://www.ean-search.org/ean/$codeStr" else "https://www.ean-search.org/")
                    list += StoreProduct(store = "EAN-Search", title = title.ifBlank { codeStr }, price = null, url = urlItem)
                }
            } else {
                // Object response with result array
                val json = org.json.JSONObject(trimmed)
                val results = json.optJSONArray("result") ?: JSONArray()
                for (i in 0 until results.length()) {
                    val o = results.getJSONObject(i)
                    val title = o.optString("name", o.optString("title", ""))
                    val codeStr = o.optString("ean", o.optString("gtin", ""))
                    val urlItem = o.optString("url", if (codeStr.isNotBlank()) "https://www.ean-search.org/ean/$codeStr" else "https://www.ean-search.org/")
                    list += StoreProduct(store = "EAN-Search", title = title.ifBlank { codeStr }, price = null, url = urlItem)
                }
            }
            return list
        }
    }

    private fun buildQueryAttempts(original: String): List<String> {
        val trimmed = original.trim()
        val lower = trimmed.lowercase()
        val noDiacritics = Normalizer.normalize(lower, Normalizer.Form.NFD).replace("\\p{Mn}".toRegex(), "")
        val tokens = noDiacritics.split(" ", "-", ",", ".", "/").map { it.filter { ch -> ch.isLetterOrDigit() } }.filter { it.length >= 3 }
        val joined = tokens.joinToString(" ")
        val attempts = linkedSetOf<String>()
        if (trimmed.isNotBlank()) attempts += trimmed
        if (lower != trimmed) attempts += lower
        if (noDiacritics != lower) attempts += noDiacritics
        if (joined.isNotBlank()) attempts += joined
        tokens.firstOrNull()?.let { attempts += it }
        // Domain-specific stopwords to drop
        attempts += joined.replace(" uht", "").trim()
        return attempts.filter { it.isNotBlank() }.toList()
    }
}

class SerpApiProvider(
    private val apiKey: String,
    private val client: OkHttpClient = OkHttpClient()
) : ProductSearchProvider {
    override suspend fun search(query: String): List<StoreProduct> {
        if (apiKey.isBlank()) return emptyList()
        val markets = listOf("Auchan", "Pingo Doce", "Lidl", "Aldi", "Mercadona")
        val q = "$query site:auchan.pt OR site:pingodoce.pt OR site:lidl.pt OR site:aldi.pt OR site:mercadona.pt"
        val url = "https://serpapi.com/search.json?engine=google&q=" + java.net.URLEncoder.encode(q, "UTF-8") + "&hl=pt&gl=pt&api_key=" + apiKey
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val json = org.json.JSONObject(resp.body?.string() ?: return emptyList())
            val organic = json.optJSONArray("organic_results") ?: JSONArray()
            val list = mutableListOf<StoreProduct>()
            for (i in 0 until organic.length()) {
                val o = organic.getJSONObject(i)
                val title = o.optString("title")
                val link = o.optString("link")
                val store = markets.firstOrNull { link.contains(it.replace(" ", "").lowercase()) || title.contains(it, ignoreCase = true) } ?: "Unknown"
                list += StoreProduct(store, title, null, link)
            }
            return list
        }
    }
}

class MockProvider : ProductSearchProvider {
    override suspend fun search(query: String): List<StoreProduct> = listOf(
        StoreProduct("Auchan", "$query - Marca A", 1.99, "https://www.auchan.pt"),
        StoreProduct("Pingo Doce", "$query - Marca B", 2.29, "https://www.pingodoce.pt"),
        StoreProduct("Lidl", "$query - Marca C", 1.79, "https://www.lidl.pt"),
    )
}

