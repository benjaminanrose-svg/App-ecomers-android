package com.example.teacherstore.api

import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun getProducts(): List<ApiProductResponse>
}
