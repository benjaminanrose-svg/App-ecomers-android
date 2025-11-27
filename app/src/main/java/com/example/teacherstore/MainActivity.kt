package com.example.teacherstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.*

import com.example.teacherstore.api.RetrofitInstance
import com.example.teacherstore.api.ProductApiRepository

import com.example.teacherstore.repository.*
import com.example.teacherstore.ui.components.DrawerHeader
import com.example.teacherstore.ui.screens.*
import com.example.teacherstore.viewmodel.*
import androidx.compose.ui.unit.dp


import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var db: TeacherAppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ----------------------- MIGRACIONES -----------------------
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN telefono TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN photoUri TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS products (
                          id TEXT NOT NULL PRIMARY KEY,
                          name TEXT NOT NULL,
                          price REAL NOT NULL,
                          image_url TEXT
                        )
                    """
                )
            }
        }

        // ----------------------- ROOM -----------------------
        db = Room.databaseBuilder(
            applicationContext,
            TeacherAppDataBase::class.java,
            "teacher_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

        // ----------------------- REPOSITORIOS LOCALES -----------------------
        val userRepo = UserRepository(db.userDao())
        val productRepo = ProductRepository(db.productDao())
        val cartRepo = CartRepository(db.cartDao())
        val userManager = UserManager(applicationContext)

        // ----------------------- REPOSITORIO API -----------------------
        val apiRepo = ProductApiRepository(
            RetrofitInstance.api
        )

        // ----------------------- UI ROOT -----------------------
        setContent {
            AppRoot(
                userRepo = userRepo,
                productRepo = productRepo,
                cartRepo = cartRepo,
                apiRepo = apiRepo,
                userManager = userManager
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    userRepo: UserRepository,
    productRepo: ProductRepository,
    cartRepo: CartRepository,
    apiRepo: ProductApiRepository,
    userManager: UserManager
) {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ----------------------- VIEWMODELS -----------------------
    val vm: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(userRepo, productRepo, userManager)
    )

    val cartVM: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo)
    )

    val apiVM: ApiViewModel = viewModel(
        factory = ApiViewModelFactory(apiRepo)
    )

    // ----------------------- LOGIN AUTOMÁTICO -----------------------
    val startDestination = rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val email = vm.checkSession()
        startDestination.value =
            if (email.isNullOrEmpty()) "login"
            else {
                vm.cargarUsuarioPorCorreo(email)
                "home"
            }
    }

    if (startDestination.value == null) {
        Surface(Modifier.fillMaxSize()) {}
        return
    }

    // ----------------------- UI ROOT -----------------------
    Surface(Modifier.fillMaxSize()) {

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {

                    DrawerHeader(vm = vm) {
                        scope.launch { drawerState.close() }
                        navController.navigate("profile")
                    }

                    NavigationDrawerItem(
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile")
                        },
                        modifier = Modifier.padding(16.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Carrito") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("cart")
                        },
                        modifier = Modifier.padding(16.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            vm.logout()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        ) {

            val route = navController.currentBackStackEntryAsState().value?.destination?.route
            val showTopBar = route !in listOf("login", "register")

            Scaffold(
                topBar = {
                    if (showTopBar) {
                        TopAppBar(
                            title = {
                                Text(
                                    when (route) {
                                        "home" -> "Contenido principal"
                                        "profile" -> "Perfil"
                                        "cart" -> "Carrito"
                                        else -> "TeacherStore"
                                    }
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.Menu, "menu")
                                }
                            },
                            actions = {
                                IconButton(onClick = { navController.navigate("cart") }) {
                                    Icon(Icons.Filled.ShoppingCart, "carrito")
                                }
                            }
                        )
                    }
                }
            ) { padding ->

                NavHost(
                    navController = navController,
                    startDestination = startDestination.value!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                )
                {


                composable("login") {
                        LoginScreen(
                            viewModel = vm,
                            onNavigateToRegister = { navController.navigate("register") },
                            onLoginSuccess = {
                                scope.launch {
                                    val email = vm.userState.value?.correo ?: vm.checkSession()
                                    if (!email.isNullOrEmpty()) {
                                        vm.cargarUsuarioPorCorreo(email)
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            viewModel = vm,
                            onRegisterSuccess = { navController.popBackStack() },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            vm = vm,
                            cartVM = cartVM,
                            apiVM = apiVM,
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onLogoutNavigate = {
                                vm.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(vm = vm) { navController.popBackStack() }
                    }

                    composable("cart") {
                        CartScreen(
                            cartViewModel = cartVM,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
