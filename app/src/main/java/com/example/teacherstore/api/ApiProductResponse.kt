package com.example.teacherstore.api

data class ApiProductResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String?
)
