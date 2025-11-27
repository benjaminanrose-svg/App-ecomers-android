package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teacherstore.repository.ProductRepository
import com.example.teacherstore.repository.UserRepository
import com.example.teacherstore.repository.UserManager

class UsuarioViewModelFactory(
    private val userRepo: UserRepository,
    private val productRepo: ProductRepository,
    private val userManager: UserManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(userRepo, productRepo, userManager) as T
        }
        throw IllegalArgumentException("UsuarioViewModelFactory: Clase desconocida ${modelClass.name}")
    }
}
