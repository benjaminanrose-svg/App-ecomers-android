package com.example.teacherstore.api

data class ApiProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)
