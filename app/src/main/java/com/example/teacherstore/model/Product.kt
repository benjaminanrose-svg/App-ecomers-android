package com.example.teacherstore.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String? = null
)
