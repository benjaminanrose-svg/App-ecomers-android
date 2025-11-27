package com.example.teacherstore.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.teacherstore.model.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart ORDER BY rowid DESC")
    fun getAll(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartEntity)

    @Update
    suspend fun update(item: CartEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cart")
    suspend fun clearCart()
}
