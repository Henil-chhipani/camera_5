package com.example.camera_5

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.ActivitySignUpBinding
import com.example.camera_5.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar


class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.signUpBtn.setOnClickListener {
            var name = binding.nameTxtInput.text.toString()
            var email = binding.emailTxtInput.text.toString()
            var password = binding.passwordTxtInput.text.toString()

            if (name.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {

                authViewModel.signup(name, email, password)

            } else {
               Snackbar.make(binding.signUpBtn,"Fill all the details",Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.loginBtn.setOnClickListener{
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        authViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                Snackbar.make( binding.signUpBtn,"Registration successful", Snackbar.LENGTH_SHORT).show()
            }
        })

        authViewModel.authError.observe(this, Observer { error ->
            if (error != null) {
                Snackbar.make(binding.signUpBtn, error, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

}