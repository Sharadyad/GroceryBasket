package com.example.grocerybasket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerybasket.data.AppDatabase
import com.example.grocerybasket.data.Item
import com.example.grocerybasket.data.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).itemDao()
    private val repo = ItemRepository(dao)

    val itemsFlow = repo.all()
    val unpurchasedFlow = repo.unpurchased()
    val purchasedFlow = repo.purchased()

    private val _isDark = MutableStateFlow(false)
    val isDark: StateFlow<Boolean> = _isDark.asStateFlow()

    fun toggleDark() { _isDark.value = !_isDark.value }

    fun addItem(name: String, qty: String, notes: String?) = viewModelScope.launch {
        if (name.isBlank()) return@launch
        repo.insert(Item(name = name.trim(), quantity = qty.trim(), notes = notes?.trim()))
    }

    fun markPurchased(item: Item, purchased: Boolean) = viewModelScope.launch {
        repo.update(item.copy(purchased = purchased))
    }

    fun delete(item: Item) = viewModelScope.launch {
        repo.delete(item)
    }

    fun clearAll() = viewModelScope.launch {
        repo.clearAll()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(app) {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ItemViewModel(app) as T
        }
    }
}
