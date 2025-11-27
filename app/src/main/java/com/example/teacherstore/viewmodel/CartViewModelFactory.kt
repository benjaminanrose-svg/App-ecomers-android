package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teacherstore.repository.CartRepository

class CartViewModelFactory(
    private val repo: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repo) as T
        }
        throw IllegalArgumentException(
            "CartViewModelFactory: Clase desconocida ${modelClass.name}"
        )
    }
}
