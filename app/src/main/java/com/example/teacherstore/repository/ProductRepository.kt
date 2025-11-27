package com.example.teacherstore.repository

import com.example.teacherstore.model.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {

    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAll()

    suspend fun add(product: ProductEntity) = dao.insert(product)

    suspend fun delete(product: ProductEntity) = dao.delete(product)

    suspend fun deleteById(id: String) = dao.deleteById(id)
}
