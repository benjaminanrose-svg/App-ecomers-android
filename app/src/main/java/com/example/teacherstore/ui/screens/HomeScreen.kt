package com.example.teacherstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.teacherstore.model.CartEntity
import com.example.teacherstore.viewmodel.ApiViewModel
import com.example.teacherstore.viewmodel.CartViewModel
import com.example.teacherstore.viewmodel.UsuarioViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: UsuarioViewModel,
    cartVM: CartViewModel,
    apiVM: ApiViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onLogoutNavigate: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    // Cargar productos automáticamente
    LaunchedEffect(Unit) {
        apiVM.fetchProducts()
    }

    val products by apiVM.products.collectAsState(initial = emptyList())
    val isLoading by apiVM.isLoading.collectAsState()
    val errorMessage by apiVM.error.collectAsState()

    // Categorías desde API
    val categories = remember(products) {
        products.map { it.category }.distinct()
    }

    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text(
            "Catálogo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Tabs de categorías
        if (categories.isNotEmpty()) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(cat.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Text("Cargando productos…")
        }

        if (!errorMessage.isNullOrBlank()) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }

        val filtered = if (categories.isNotEmpty())
            products.filter { it.category == categories[selectedTab] }
        else emptyList()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered) { p ->
                ProductCard(
                    title = p.title,
                    price = p.price,
                    image = p.image,
                    onAdd = {
                        cartVM.add(
                            CartEntity(
                                id = p.id.toString(),
                                name = p.title,
                                price = p.price,
                                quantity = 1
                            )
                        )
                    }
                )
            }
        }
    }
}

// ---------------------------
//   ESTA FUNCIÓN VA AFUERA
// ---------------------------
@Composable
fun ProductCard(
    title: String,
    price: Double,
    image: String,
    onAdd: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = image,
                contentDescription = title,
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Text(
                text = "$${"%.2f".format(price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text("Agregar")
            }
        }
    }
}
