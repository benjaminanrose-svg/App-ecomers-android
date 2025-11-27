package com.example.teacherstore

import com.example.teacherstore.repository.UserManager

class FakeUserManager : UserManager {

    private var email: String? = null

    override suspend fun saveLoggedInEmail(email: String) {
        this.email = email
    }

    override suspend fun getLoggedInEmail(): String? {
        return email
    }

    override suspend fun clearSession() {
        email = null
    }
}
