package com.example.camera_5.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camera_5.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> get() = _authError

    private val _resetStatus = MutableLiveData<String?>()
    val resetStatus: LiveData<String?> get() = _resetStatus


    private val _checkUserStatus = MutableLiveData<String?>()
    val checkUserStatus: LiveData<String?> get() = _checkUserStatus

    init {
        // Set initial value of _user to the current user, which could be null if not logged in
        _user.value = userRepository.getCurrentUser()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if (result != null) {
                _user.value = result
            } else {
                _authError.value = "Login failed"
            }
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.signup(name, email, password)
            if (result != null) {
                _user.value = result
            } else {
                _authError.value = "Signup failed"
            }
        }
    }


    fun logout() {
        _user.value = null
        userRepository.logout()
    }

    fun getCurrentUser() {
        _user.value = userRepository.getCurrentUser()

    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            userRepository.resetPassword(email) { status ->
                _resetStatus.value = status
            }
        }
    }


}