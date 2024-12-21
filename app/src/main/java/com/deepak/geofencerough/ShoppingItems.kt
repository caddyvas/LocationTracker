package com.deepak.geofencerough

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.deepak.geofencerough.activity.MapsActivity
import com.deepak.geofencerough.viewModel.MainViewModel


data class ShoppingItems(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

// This annotation tells the compiler that the function is meant for UI construction (build UI with
// kotlin code which is intuitive and flexible)
@Composable
fun ShoppingList(context: Context?) {
    var sItems by remember {
        mutableStateOf(listOf<ShoppingItems>())
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var itemName by remember {
        mutableStateOf("")
    }
    var itemQuantity by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.padding(all = 2.dp)
            ) {
                Text(text = "ADD ITEMS")
            }

            Button(
                onClick = { openMapsActivity(context) },
                modifier = Modifier.padding(all = 10.dp)
            ) {
                Text(text = "MAP SCREEN")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { displayLocationDetails() },
                modifier = Modifier.padding(all = 2.dp)
            ) {
                Text(text = "VIEW LOCATIONS")
            }

            Button(
                onClick = { },
                modifier = Modifier.padding(all = 10.dp)
            ) {
                Text(text = "DELETE LOCATIONS")
            }
        }
        // lazycolumn is a vertically scrolling list that only composes and lays out the currently
        // visible items. It's similar to a recyclerview
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            items(sItems) { item ->
                if (item.isEditing) {
                    ShoppingItemEditor(
                        item = item,
                        onEditComplete = { editedName, editedQuantity ->
                            sItems = sItems.map {
                                it.copy(isEditing = false)
                            }
                            val editedItem = sItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        })
                } else {
                    ShoppingListItem(item = item, onEditClick = {
                        sItems = sItems.map {
                            it.copy(isEditing = it.id == item.id)
                        }
                    }, onDeleteClick = {
                        sItems = sItems - item
                    })
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (itemName.isNotBlank()) {
                        val newItem = ShoppingItems(
                            id = sItems.size + 1,
                            name = itemName,
                            quantity = itemQuantity.toInt()
                        )
                        sItems = sItems + newItem
                        showDialog = false
                        itemName = ""
                        itemQuantity = ""
                    }
                }) {
                    Text(text = "Add")
                }
                Button(onClick = {
                    showDialog = false
                    itemName = ""
                    itemQuantity = ""
                }) {
                    Text(text = "Cancel")
                }
            }
        },
            title = { Text(text = "Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItems, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.White)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(20.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(20.dp))
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = { onEditClick() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = { onDeleteClick() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Preview
@Composable
fun PreviewOfShoppingList() {
    val item = ShoppingItems(22, "Vegetables", 3, false)
    ShoppingListItem(item, { Unit }, { Unit })
}

@Composable
fun ShoppingItemEditor(item: ShoppingItems, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember {
        mutableStateOf(item.name)
    }
    var editedQuantity by remember {
        mutableStateOf(item.quantity.toString())
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(onClick = {
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
        }) {
            Text(text = "Save")
        }
    }
}

@Preview
@Composable
fun ShoppingListPreview() {
    ShoppingList(null)
}

fun openMapsActivity(context: Context?) {
    val intent = Intent(context, MapsActivity::class.java)
    context?.startActivity(intent)
}

fun displayLocationDetails() {
    val mainViewModel = MainViewModel()
    val locationDetails = mainViewModel.queryLocationDetails()
    Log.d(
        "ShoppingList - ${locationDetails.size}",
        locationDetails[0].locationName + "--" + locationDetails[0].address
    )
}
