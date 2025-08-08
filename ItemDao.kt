package com.example.grocerybasket.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY purchased ASC, name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE purchased = 1 ORDER BY name COLLATE NOCASE ASC")
    fun getPurchased(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE purchased = 0 ORDER BY name COLLATE NOCASE ASC")
    fun getUnpurchased(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items")
    suspend fun clearAll()
}
