package com.example.teacherstore.repository

import com.example.teacherstore.model.Users
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun obtenerUsuarioPorCorreo(correo: String): Users? {
        return userDao.getByEmail(correo)
    }

    fun obtenerUsuarioPorCorreoFlow(correo: String): Flow<Users?> {
        return userDao.getByEmailFlow(correo)
    }

    suspend fun insert(user: Users) {
        userDao.insert(user)
    }

    suspend fun update(user: Users) {
        userDao.update(user)   // ⬅️ Aquí Room ya actualiza también photoUri
    }

    suspend fun deleteByEmail(correo: String) {
        userDao.deleteByEmail(correo)
    }
}
