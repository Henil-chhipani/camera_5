package com.example.camera_5.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camera_5.data.model.UserModel
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


    private val _userProfile = MutableLiveData<UserModel?>()
    val userProfile: LiveData<UserModel?> get() = _userProfile

    private val _isLogin = MutableLiveData<Boolean?>()
    val islogin : LiveData<Boolean?> get() = _isLogin

    init {
        // Set initial value of _user to the current user, which could be null if not logged in
        _user.value = userRepository.getCurrentUser()
        loadUserProfile()
    }

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            if (result != null) {
                _user.value = result
                _isLogin.value = true;
            } else {
                _authError.value = "Login failed"
            }
        }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        image: String,
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val user = userRepository.signup(name, email, password,image)
            if (user != null) {
                _user.value = user
                loadUserProfile()
                callback(true, null)
            } else {
                callback(false, "Signup failed")
            }
        }
    }

    fun logout() {
        _user.value = null
        _userProfile.value = null
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

    private fun loadUserProfile() {
        val currentUser = userRepository.getCurrentUser()
        currentUser?.let { user ->
            viewModelScope.launch {
                val profile = userRepository.getUserProfile(user.uid)
                Log.d("profile", "loadUserProfile: $profile")
                _userProfile.value = profile
            }
        }
    }

    fun updateUserProfile(user: UserModel, callback: (Boolean, String?) -> Unit) {
        val currentUser = userRepository.getCurrentUser()
        currentUser?.let { userAuth ->
            viewModelScope.launch {
                try {
                    userRepository.updateUserProfile(userAuth.uid, user)
                    _userProfile.value = user
                    callback(true, "Profile updated")
                } catch (e: Exception) {
                    callback(false, e.message)
                }
            }
        }
    }


}