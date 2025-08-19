package com.smartgrocery.pantry.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import com.smartgrocery.pantry.BuildConfig

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
        // EAN-Search supports query by product name. The API shape can vary.
        val url = "$baseUrl?op=search&format=json&token=$token&term=" + java.net.URLEncoder.encode(query, "UTF-8")
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val body = resp.body?.string() ?: return emptyList()
            val json = org.json.JSONObject(body)
            val results = json.optJSONArray("result") ?: JSONArray()
            val list = mutableListOf<StoreProduct>()
            for (i in 0 until results.length()) {
                val o = results.getJSONObject(i)
                val title = o.optString("name", o.optString("title", query))
                val code = o.optString("ean", o.optString("gtin", ""))
                val urlItem = o.optString("url", "https://www.ean-search.org/ean/$code")
                list += StoreProduct(store = "EAN-Search", title = title, price = null, url = urlItem)
            }
            return list
        }
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

