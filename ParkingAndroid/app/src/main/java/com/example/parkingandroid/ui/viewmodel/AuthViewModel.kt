package com.example.parkingandroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.parkingandroid.data.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn

    fun login(email: String, password: String): Boolean {
        return repository.login(email, password)
    }

    fun logout() {
        repository.logout()
    }
}
