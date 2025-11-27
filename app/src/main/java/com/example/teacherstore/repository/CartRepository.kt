package com.example.teacherstore.repository

import com.example.teacherstore.model.CartEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val dao: CartDao) {

    fun getAll(): Flow<List<CartEntity>> = dao.getAll()

    suspend fun insert(item: CartEntity) = dao.insert(item)

    suspend fun update(item: CartEntity) = dao.update(item)

    suspend fun deleteById(id: String) = dao.deleteById(id)

    suspend fun clear() = dao.clearCart()
}
