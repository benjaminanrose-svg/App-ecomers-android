package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherstore.api.ProductApiRepository
import com.example.teacherstore.api.ApiProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiViewModel(
    private val repo: ProductApiRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<ApiProductResponse>>(emptyList())
    val products: StateFlow<List<ApiProductResponse>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = repo.fetchProducts()
                _products.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

