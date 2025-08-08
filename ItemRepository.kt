package com.example.grocerybasket.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val dao: ItemDao) {
    fun all(): Flow<List<Item>> = dao.getAll()
    fun unpurchased(): Flow<List<Item>> = dao.getUnpurchased()
    fun purchased(): Flow<List<Item>> = dao.getPurchased()
    suspend fun insert(item: Item) = dao.insert(item)
    suspend fun update(item: Item) = dao.update(item)
    suspend fun delete(item: Item) = dao.delete(item)
    suspend fun clearAll() = dao.clearAll()
}
