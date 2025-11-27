package com.example.teacherstore.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.teacherstore.model.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE correo = :email LIMIT 1")
    fun getByEmailFlow(email: String): Flow<Users?>

    @Query("SELECT * FROM users WHERE correo = :email LIMIT 1")
    suspend fun getByEmail(email: String): Users?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Users)

    @Query("DELETE FROM users WHERE correo = :email")
    suspend fun deleteByEmail(email: String)

    @Update
    suspend fun update(user: Users)
}
