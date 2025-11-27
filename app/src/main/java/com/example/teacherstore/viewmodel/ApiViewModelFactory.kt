package com.example.teacherstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teacherstore.api.ProductApiRepository

class ApiViewModelFactory(
    private val repo: ProductApiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiViewModel::class.java)) {
            return ApiViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
