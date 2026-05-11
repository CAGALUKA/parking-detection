package com.example.parkingandroid.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun login(email: String, password: String): Boolean {
        // Örnek hesap: kullanici@example.com / 123456
        return if (email == "kullanici@example.com" && password == "123456") {
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
