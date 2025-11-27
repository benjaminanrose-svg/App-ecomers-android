package com.example.teacherstore.repository

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestión simple de sesión en SharedPreferences.
 * Métodos usados por UsuarioViewModel: getLoggedInEmail, saveLoggedInEmail, clearSession
 */
class UserManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("teacher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN_EMAIL = "key_logged_in_email"
    }

    fun saveLoggedInEmail(email: String) {
        prefs.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply()
    }

    fun getLoggedInEmail(): String? {
        return prefs.getString(KEY_LOGGED_IN_EMAIL, null)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_LOGGED_IN_EMAIL).apply()
    }
}
