package com.example.teacherstore

import com.example.teacherstore.model.Users
import com.example.teacherstore.repository.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserDao : UserDao {

    private val users = mutableListOf<Users>()
    private val userFlow = MutableStateFlow<Users?>(null)

    override fun getByEmailFlow(email: String): Flow<Users?> {
        userFlow.value = users.find { it.correo == email }
        return userFlow
    }

    override suspend fun getByEmail(email: String): Users? {
        return users.find { it.correo == email }
    }

    override suspend fun insert(user: Users) {
        val existing = users.indexOfFirst { it.correo == user.correo }
        if (existing >= 0) {
            users[existing] = user
        } else {
            users.add(user)
        }
        userFlow.value = user
    }

    override suspend fun deleteByEmail(email: String) {
        val removed = users.removeIf { it.correo == email }
        if (removed) userFlow.value = null
    }

    override suspend fun update(user: Users) {
        insert(user)
    }
}
