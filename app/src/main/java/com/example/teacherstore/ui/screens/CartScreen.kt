package com.example.teacherstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.teacherstore.viewmodel.CartViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit
) {
    val cartItems = cartViewModel.items.collectAsState(initial = emptyList()).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (cartItems.isEmpty()) {
                Text("Tu carrito está vacío")
                return@Column
            }

            cartItems.forEach { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(item.name)
                            Text("$${item.price}")
                            Text("Cantidad: ${item.quantity}")
                        }

                        IconButton(
                            onClick = { cartViewModel.remove(item.id) }
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { cartViewModel.clear() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vaciar carrito")
            }
        }
    }
}
