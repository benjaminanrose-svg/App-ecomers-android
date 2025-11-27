package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherstore.model.Product
import com.example.teacherstore.model.ProductEntity
import com.example.teacherstore.model.Users
import com.example.teacherstore.repository.ProductRepository
import com.example.teacherstore.repository.UserRepository
import com.example.teacherstore.repository.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val userRepo: UserRepository,
    private val productRepo: ProductRepository,
    private val userManager: UserManager
) : ViewModel() {

    // ============================================================
    // USUARIO
    // ============================================================

    private val _userState = MutableStateFlow<Users?>(null)
    val userState: StateFlow<Users?> = _userState

    fun cargarUsuarioPorCorreo(email: String) {
        viewModelScope.launch {
            try {
                _userState.value = userRepo.obtenerUsuarioPorCorreo(email)
            } catch (_: Throwable) {
                _userState.value = null
            }
        }
    }

    suspend fun checkSession(): String? = userManager.getLoggedInEmail()

    fun logout() {
        viewModelScope.launch {
            userManager.clearSession()
            _userState.value = null
        }
    }

    fun updateUser(
        nombre: String,
        correo: String,
        direccion: String?,
        telefono: String?,
        photoUri: String?
    ) {
        viewModelScope.launch {
            try {
                val current = userRepo.obtenerUsuarioPorCorreo(correo)
                if (current != null) {
                    val updated = current.copy(
                        nombre = nombre,
                        direccion = direccion ?: current.direccion,
                        telefono = telefono ?: current.telefono,
                        photoUri = photoUri ?: current.photoUri
                    )
                    userRepo.update(updated)
                    _userState.value = updated
                }
            } catch (_: Throwable) { }
        }
    }

    suspend fun register(
        nombre: String,
        correo: String,
        contrasena: String,
        direccion: String,
        telefono: String
    ): Boolean {
        return try {
            if (userRepo.obtenerUsuarioPorCorreo(correo) != null) return false

            val newUser = Users(
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                direccion = direccion,
                telefono = telefono,
                photoUri = null
            )

            userRepo.insert(newUser)
            userManager.saveLoggedInEmail(correo)
            _userState.value = newUser
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val user = userRepo.obtenerUsuarioPorCorreo(email)
            val ok = user?.contrasena == password
            if (ok) {
                userManager.saveLoggedInEmail(email)
                _userState.value = user
            }
            ok
        } catch (_: Throwable) {
            false
        }
    }

    // ============================================================
    // PRODUCTOS (ROOM)
    // ============================================================

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    init {
        // Observa Room en tiempo real
        viewModelScope.launch {
            productRepo.getAllProducts().collectLatest { list ->
                _products.value = list.map { entity ->
                    Product(
                        id = entity.id,
                        name = entity.name,
                        price = entity.price,
                        imageUrl = entity.imageUrl
                    )
                }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            productRepo.add(
                ProductEntity(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl
                )
            )
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepo.delete(
                ProductEntity(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl
                )
            )
        }
    }

    fun deleteProductById(id: String) {
        viewModelScope.launch {
            productRepo.deleteById(id)
        }
    }

    fun observeUserByEmail(email: String) {
        cargarUsuarioPorCorreo(email)
    }
}
