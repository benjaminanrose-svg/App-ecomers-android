package com.example.teacherstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.teacherstore.api.ProductApiRepository
import com.example.teacherstore.api.RetrofitInstance
import com.example.teacherstore.repository.CartRepository
import com.example.teacherstore.repository.ProductRepository
import com.example.teacherstore.repository.TeacherAppDataBase
import com.example.teacherstore.repository.UserManager
import com.example.teacherstore.repository.UserRepository
import com.example.teacherstore.ui.screens.CartScreen
import com.example.teacherstore.ui.screens.HomeScreen
import com.example.teacherstore.ui.screens.LoginScreen
import com.example.teacherstore.ui.screens.ProfileScreen
import com.example.teacherstore.ui.screens.RegisterScreen
import com.example.teacherstore.viewmodel.ApiViewModel
import com.example.teacherstore.viewmodel.ApiViewModelFactory
import com.example.teacherstore.viewmodel.CartViewModel
import com.example.teacherstore.viewmodel.CartViewModelFactory
import com.example.teacherstore.viewmodel.UsuarioViewModel
import com.example.teacherstore.viewmodel.UsuarioViewModelFactory
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

        // Scaffold simple: la barra superior la maneja cada pantalla (Home, Perfil, Carrito)
        androidx.compose.material3.Scaffold { padding ->

            NavHost(
                navController = navController,
                startDestination = startDestination.value!!,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

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
                        }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() }
                    )
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
