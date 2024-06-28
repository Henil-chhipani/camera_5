package com.example.camera_5.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.camera_5.R
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.FragmentForgotPasswordBinding
import com.example.camera_5.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar


class ForgotPasswordFragment : Fragment() {


    private lateinit var binding: FragmentForgotPasswordBinding

    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.resetBtn.setOnClickListener {
            var email = binding.emailTxtInput.text.toString()

            Log.d("email", "$email ")
            if (email.isEmpty()) {
                Snackbar.make(view, "Please enter your email", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                authViewModel.resetPassword(email)
                val res = authViewModel.resetStatus.value.toString()
                Snackbar.make(view, res, Snackbar.LENGTH_SHORT).show()
            }
        }


    }
}
