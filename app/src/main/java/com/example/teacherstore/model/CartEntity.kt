package com.example.teacherstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val quantity: Int = 1
)
