package com.example.grocerybasket.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.grocerybasket.ItemViewModel
import com.example.grocerybasket.data.Item
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(vm: ItemViewModel) {
    val scaffoldState = rememberScaffoldState()
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Status") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grocery Basket") },
                actions = {
                    IconButton(onClick = { vm.toggleDark() }) {
                        Text("Theme")
                    }
                    IconButton(onClick = {
                        coroutine.launch {
                            vm.clearAll()
                            Toast.makeText(context, "Cleared all items", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                onClick = { showDialog = true }
            )
        },
        bottomBar = {
            BottomAppBar() {
                Text("List", modifier = Modifier.padding(12.dp))
                Spacer(Modifier.weight(1f))
                Text("Completed", modifier = Modifier.padding(12.dp))
            }
        },
        scaffoldState = scaffoldState
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Simple combined list view (for demo). Use proper nav for real app.
            val items by vm.itemsFlow.collectAsState(initial = emptyList())
            GroceryList(items = items, onToggle = { item, checked ->
                vm.markPurchased(item, checked)
                coroutine.launch {
                    val msg = if (checked) "Marked purchased" else "Marked unpurchased"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }, onDelete = { item ->
                vm.delete(item)
                coroutine.launch {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    if (showDialog) {
        AddItemDialog(onAdd = { name, qty, notes ->
            vm.addItem(name, qty, notes)
            showDialog = false
            Toast.makeText(LocalContext.current, "Item added", Toast.LENGTH_SHORT).show()
        }, onDismiss = { showDialog = false })
    }
}

@Composable
fun GroceryList(items: List<Item>, onToggle: (Item, Boolean) -> Unit, onDelete: (Item) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(items, key = { it.id }) { item ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Row(modifier = Modifier
                    .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = item.purchased, onCheckedChange = { onToggle(item, it) })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                        if (!item.notes.isNullOrBlank()) {
                            Text(text = item.notes!!, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(text = item.quantity, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(onAdd: (String, String, String?) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) return@TextButton
                onAdd(name, qty, if (notes.isBlank()) null else notes)
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Item") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item name*") })
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Quantity") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
            }
        }
    )
}
