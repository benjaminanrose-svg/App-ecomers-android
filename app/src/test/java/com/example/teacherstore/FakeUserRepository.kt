package com.example.teacherstore

import com.example.teacherstore.model.Users
import com.example.teacherstore.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserRepository : UserRepository(FakeUserDao()) {

    private val fakeDao = FakeUserDao()
    private val userFlow = MutableStateFlow<Users?>(null)

    override suspend fun insert(user: Users) {
        fakeDao.insert(user)
        userFlow.value = user
    }

    override suspend fun update(user: Users) {
        fakeDao.update(user)
        userFlow.value = user
    }

    override suspend fun deleteByEmail(correo: String) {
        fakeDao.deleteByEmail(correo)
        userFlow.value = null
    }

    override fun obtenerUsuarioPorCorreoFlow(correo: String): Flow<Users?> {
        return fakeDao.getByEmailFlow(correo)
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): Users? {
        return fakeDao.getByEmail(correo)
    }
}
