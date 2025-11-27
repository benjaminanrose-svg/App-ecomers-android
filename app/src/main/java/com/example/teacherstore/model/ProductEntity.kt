package com.example.teacherstore.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
) {
    fun toModel() = Product(id = id, name = name, price = price, imageUrl = imageUrl)
}
