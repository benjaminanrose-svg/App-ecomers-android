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

    // Flow para observar cambios del usuario (por correo)
    @Query("SELECT * FROM users WHERE correo = :email LIMIT 1")
    fun getByEmailFlow(email: String): Flow<Users?>

    // Obtener el usuario una sola vez (suspend)
    @Query("SELECT * FROM users WHERE correo = :email LIMIT 1")
    suspend fun getByEmail(email: String): Users?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Users)

    // ESTE update actualiza también photoUri porque forma parte de Users
    @Update
    suspend fun update(user: Users)

    @Query("DELETE FROM users WHERE correo = :email")
    suspend fun deleteByEmail(email: String)
}
