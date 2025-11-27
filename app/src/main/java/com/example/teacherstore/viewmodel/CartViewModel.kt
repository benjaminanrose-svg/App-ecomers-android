package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherstore.model.CartEntity
import com.example.teacherstore.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull


class CartViewModel(private val repo: CartRepository) : ViewModel() {

    val items: Flow<List<CartEntity>> = repo.getAll()

    /**
     * Agrega un producto nuevo al carrito o incrementa la cantidad si ya existe.
     */
    fun addOrIncrement(id: String, name: String, price: Double) {
        viewModelScope.launch {
            val current = repo.getAll()        // ← Flow, hay que leerlo una vez
                .let { it.firstOrNull() }      // necesitas importar kotlinx.coroutines.flow.firstOrNull
                ?.find { it.id == id }

            if (current == null) {
                // No existe en el carrito → crear item nuevo
                repo.insert(CartEntity(id, name, price, quantity = 1))
            } else {
                // Ya existe → incrementamos cantidad
                repo.update(
                    current.copy(quantity = current.quantity + 1)
                )
            }
        }
    }

    /**
     * Inserta un item directamente (si quieres usarlo manualmente)
     */
    fun add(item: CartEntity) {
        viewModelScope.launch {
            repo.insert(item)
        }
    }

    /**
     * Elimina un solo item del carrito por ID.
     */
    fun remove(id: String) {
        viewModelScope.launch {
            repo.deleteById(id)
        }
    }

    /**
     * Vaciar carrito completo
     */
    fun clear() {
        viewModelScope.launch {
            repo.clear()
        }
    }
}
