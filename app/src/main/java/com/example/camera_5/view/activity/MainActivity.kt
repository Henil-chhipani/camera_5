package com.example.camera_5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.ActivityMainBinding
import com.example.camera_5.view.Adapter.BottomNavAdapter
import com.example.camera_5.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        Log.d("user", "onCreate: ${authViewModel.getCurrentUser()}")

        val bottomNavigationView = binding.bottomNavigationView
        val viewPager = binding.viewPager

        val adapter = BottomNavAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> viewPager.currentItem = 0
                R.id.camera -> viewPager.currentItem = 1
                R.id.gallery -> viewPager.currentItem = 2

            }
            true
        }
    }
}