package com.smartgrocery.pantry.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryItemDao {
    @Query("SELECT * FROM pantry_items ORDER BY CASE WHEN expirationDate IS NULL THEN 1 ELSE 0 END, expirationDate ASC")
    fun observeAll(): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PantryItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM pantry_items")
    suspend fun getAllOnce(): List<PantryItemEntity>
}

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY plannedDate ASC")
    fun observeAll(): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(meals: List<MealEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealEntity)

    @Query("DELETE FROM meals")
    suspend fun clearAll()

    @Query("SELECT * FROM meals")
    suspend fun getAllOnce(): List<MealEntity>
}

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY title ASC")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM shopping_items")
    suspend fun clearAll()
}

