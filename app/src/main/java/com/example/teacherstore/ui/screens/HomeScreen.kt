package com.example.teacherstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.teacherstore.model.CartEntity
import com.example.teacherstore.viewmodel.ApiViewModel
import com.example.teacherstore.viewmodel.CartViewModel
import com.example.teacherstore.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: UsuarioViewModel,
    cartVM: CartViewModel,
    apiVM: ApiViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onLogoutNavigate: () -> Unit,
) {
    // Cargar productos una vez
    LaunchedEffect(Unit) {
        apiVM.fetchProducts()
    }

    val products by apiVM.products.collectAsState(initial = emptyList())
    val isLoading by apiVM.isLoading.collectAsState()
    val errorMessage by apiVM.error.collectAsState()

    // Estado de usuario para la foto de perfil
    val userState by vm.userState.collectAsState()
    val profilePhoto = userState?.photoUri

    // Categorías desde API (únicas)
    val categories = remember(products) {
        products.map { it.category }.distinct()
    }

    var selectedTab by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Asegurar que el selectedTab siempre esté dentro de rango
    LaunchedEffect(categories.size) {
        if (categories.isNotEmpty() && selectedTab > categories.lastIndex) {
            selectedTab = 0
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mi Tiendita",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                // Ya NO hay navigationIcon de menú porque quitamos el Drawer
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }

                    // Botón de perfil con foto
                    IconButton(onClick = onNavigateToProfile) {
                        if (!profilePhoto.isNullOrEmpty()) {
                            AsyncImage(
                                model = profilePhoto,
                                contentDescription = "Perfil",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil"
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Más opciones"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    showMenu = false
                                    onLogoutNavigate()
                                }
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFf5f7fb),
                            Color(0xFFe3f2fd)
                        )
                    )
                )
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {

                Text(
                    text = "Catálogo",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Tabs de categorías
                if (categories.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
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

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    !errorMessage.isNullOrBlank() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ocurrió un error al cargar los productos",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = {
                                        scope.launch { apiVM.fetchProducts() }
                                    }) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        val filtered = if (categories.isNotEmpty()) {
                            products.filter { it.category == categories[selectedTab] }
                        } else {
                            products
                        }

                        if (filtered.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay productos disponibles en esta categoría.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
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
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Producto agregado al carrito"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    title: String,
    price: Double,
    image: String,
    onAdd: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
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
                    .padding(bottom = 4.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(Modifier.height(4.dp))

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
