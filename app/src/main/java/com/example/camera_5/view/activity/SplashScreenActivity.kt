package com.example.camera_5

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.ActivitySplashScreenBinding
import com.example.camera_5.viewModel.AuthViewModel

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        var user = authViewModel.getCurrentUser();
        if (user != null) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}