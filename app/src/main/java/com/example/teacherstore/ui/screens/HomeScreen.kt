package com.example.teacherstore.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.teacherstore.model.CartEntity
import com.example.teacherstore.model.Product
import com.example.teacherstore.viewmodel.ApiViewModel
import com.example.teacherstore.viewmodel.CartViewModel
import com.example.teacherstore.viewmodel.UsuarioViewModel


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

    val apiProducts by apiVM.products.collectAsState(initial = emptyList())
    val isLoading by apiVM.isLoading.collectAsState(initial = false)
    val errorMessage by apiVM.error.collectAsState(initial = null)

    // --- CARGA AUTOMÁTICA ---
    LaunchedEffect(Unit) {
        apiVM.fetchProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text(
            "Catálogo",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(12.dp))

        // ERRORES / CARGA
        if (isLoading) {
            LoadingSkeletonGrid()
        }

        if (!errorMessage.isNullOrBlank()) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }

        // GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(apiProducts, key = { it.id }) { apiProd ->

                val product = Product(
                    id = apiProd.id.toString(),
                    name = apiProd.title,
                    price = apiProd.price,
                    imageUrl = apiProd.image
                )

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            cartVM.add(
                                CartEntity(
                                    id = product.id,
                                    name = product.name,
                                    price = product.price,
                                    quantity = 1
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(8.dp))

            Text(
                product.name,
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "USD ${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Agregar")
            }
        }
    }
}




// --- Skeleton Loader mientras carga ---
@Composable
fun LoadingSkeletonGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            )
        }
    }
}
