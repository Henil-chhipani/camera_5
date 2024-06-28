package com.example.camera_5

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.ActivityLoginBinding
import com.example.camera_5.view.fragment.ForgotPasswordFragment
import com.example.camera_5.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        var user = authViewModel.getCurrentUser();


        binding.loginBtn.setOnClickListener {
            var email = binding.emailTxtInput.text.toString()
            var password = binding.passwordTxtInput.text.toString()

            if(email.isNotEmpty() || password.isNotEmpty()){

                authViewModel.login(email,password)
            }else{
                Snackbar.make( binding.loginBtn," Fill all details", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.forgotPassword.setOnClickListener{
          // do it with view pager fragment name is frogot password
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.viewPager, ForgotPasswordFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.signUpBtn.setOnClickListener{
            var intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        authViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                // Navigate to next screen
                var intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                Snackbar.make(binding.loginBtn, "Login successful", Snackbar.LENGTH_SHORT).show()
            }
        })

        authViewModel.authError.observe(this, Observer { error ->
            if (error != null) {
                Snackbar.make(binding.loginBtn, error, Snackbar.LENGTH_SHORT).show()
            }
        })




    }
}