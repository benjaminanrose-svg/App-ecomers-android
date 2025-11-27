package com.example.teacherstore.api

class ProductApiRepository(
    private val api: ApiService
) {

    suspend fun fetchProducts(): List<ApiProductResponse> {
        return api.getProducts()
    }
}
