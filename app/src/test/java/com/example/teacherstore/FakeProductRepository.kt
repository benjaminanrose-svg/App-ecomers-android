package com.example.teacherstore

import com.example.teacherstore.model.ProductEntity
import com.example.teacherstore.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProductRepository : ProductRepository {

    private val products = mutableListOf<ProductEntity>()
    private val flow = MutableStateFlow<List<ProductEntity>>(emptyList())

    override fun getAllProducts(): Flow<List<ProductEntity>> = flow

    override suspend fun add(product: ProductEntity) {
        products.add(product)
        flow.value = products.toList()
    }

    override suspend fun delete(product: ProductEntity) {
        products.removeIf { it.id == product.id }
        flow.value = products.toList()
    }

    override suspend fun deleteById(id: String) {
        products.removeIf { it.id == id }
        flow.value = products.toList()
    }
}
