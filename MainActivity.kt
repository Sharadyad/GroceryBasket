package com.example.grocerybasket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.grocerybasket.ui.theme.GroceryBasketTheme

class MainActivity : ComponentActivity() {
    private val vm: ItemViewModel by viewModels { ItemViewModel.Factory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = vm.isDark.collectAsState()
            GroceryBasketTheme(darkTheme = isDark.value) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppScaffold(vm)
                }
            }
        }
    }
}
