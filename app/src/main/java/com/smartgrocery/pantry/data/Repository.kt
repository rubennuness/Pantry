package com.smartgrocery.pantry.data

import android.content.Context
import com.smartgrocery.pantry.data.db.AppDatabase
import com.smartgrocery.pantry.data.db.MealEntity
import com.smartgrocery.pantry.data.db.PantryItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

interface Repository {
    val itemsFlow: Flow<List<PantryItem>>
    val mealsFlow: Flow<List<Meal>>

    suspend fun upsertItem(item: PantryItem)
    suspend fun removeItem(id: String)
    suspend fun generateFiveDayPlan(todayIso: String): List<Meal>
    suspend fun exportJson(): String
}

class RoomRepository private constructor(context: Context) : Repository {
    private val db = AppDatabase.get(context)
    private val json = Json { prettyPrint = true }

    override val itemsFlow: Flow<List<PantryItem>> = db.pantryItemDao().observeAll().map { entities ->
        entities.map { it.toModel() }
    }

    override val mealsFlow: Flow<List<Meal>> = db.mealDao().observeAll().map { entities ->
        entities.map { it.toModel() }
    }

    override suspend fun upsertItem(item: PantryItem) {
        db.pantryItemDao().upsert(item.toEntity())
    }

    override suspend fun removeItem(id: String) {
        db.pantryItemDao().deleteById(id)
    }

    override suspend fun generateFiveDayPlan(todayIso: String): List<Meal> {
        val items = db.pantryItemDao().getAllOnce()
        val plan = mutableListOf<MealEntity>()
        val candidates = items.sortedBy { it.expirationDate ?: "9999-12-31" }
        for (i in 0 until 5) {
            val date = java.time.LocalDate.parse(todayIso).plusDays(i.toLong()).toString()
            val top = candidates.drop(i).firstOrNull()
            plan += MealEntity(
                id = UUID.randomUUID().toString(),
                title = top?.let { "Use ${it.name}" } ?: "Chef's Choice",
                description = top?.expirationDate?.let { "Expires $it" },
                plannedDate = date,
                usedItemIdsJson = json.encodeToString(top?.let { listOf(it.id) } ?: emptyList())
            )
        }
        db.mealDao().clearAll()
        db.mealDao().upsertAll(plan)
        return plan.map { it.toModel() }
    }

    override suspend fun exportJson(): String {
        val items = db.pantryItemDao().getAllOnce().map { it.toModel() }
        val meals = db.mealDao().getAllOnce().map { it.toModel() }
        return json.encodeToString(mapOf("items" to items, "meals" to meals))
    }

    private fun PantryItemEntity.toModel(): PantryItem = PantryItem(
        id = id,
        name = name,
        category = category,
        quantity = quantity,
        unit = unit,
        expirationDate = expirationDate,
        purchasedDate = purchasedDate,
    )

    private fun MealEntity.toModel(): Meal = Meal(
        id = id,
        title = title,
        description = description,
        plannedDate = plannedDate,
        usedItemIds = try { Json.decodeFromString<List<String>>(usedItemIdsJson) } catch (_: Throwable) { emptyList() }
    )

    private fun PantryItem.toEntity(): PantryItemEntity = PantryItemEntity(
        id = id.ifEmpty { UUID.randomUUID().toString() },
        name = name,
        category = category,
        quantity = quantity,
        unit = unit,
        expirationDate = expirationDate,
        purchasedDate = purchasedDate,
    )

    companion object {
        @Volatile private var instance: RoomRepository? = null
        fun get(context: Context): RoomRepository = instance ?: synchronized(this) {
            instance ?: RoomRepository(context.applicationContext).also { instance = it }
        }
    }
}

