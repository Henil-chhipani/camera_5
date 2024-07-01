package com.example.camera_5

import android.app.Activity
import android.app.Activity.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.camera_5.Utilities.ImageUtils
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.ActivitySignUpBinding
import com.example.camera_5.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream


class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel
    private var selectedImageUri: Uri? = null

    companion object {
        const val REQUEST_CODE_IMAGE_PICK = 1
        const val MAX_IMAGE_SIZE = 1024
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.profileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)


        }


        binding.signUpBtn.setOnClickListener {

            binding.lodingIcon.visibility = View.VISIBLE
            val name = binding.nameTxtInput.text.toString().trim()
            val email = binding.emailTxtInput.text.toString().trim()
            val password = binding.passwordTxtInput.text.toString().trim()
            val image =
                if (selectedImageUri != null) processImageUri(selectedImageUri!!) else ""

            Log.d("image", "onCreate: $image")
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && image.isNotEmpty()) {
                authViewModel.signup(name, email, password, image) { success, message ->
                    binding.lodingIcon.visibility = View.GONE
                    if (success) {
                        Snackbar.make(
                            binding.signUpBtn,
                            "Registration successful",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        var intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Snackbar.make(
                            binding.signUpBtn,
                            message ?: "Registration failed",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Snackbar.make(binding.signUpBtn, "Fill all the details", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.loginBtn.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        authViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                Snackbar.make(binding.signUpBtn, "Registration successful", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        authViewModel.authError.observe(this, Observer { error ->
            if (error != null) {
                Snackbar.make(binding.signUpBtn, error, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.addImgTxt.visibility = View.GONE
                Glide.with(this).load(uri).transform(CircleCrop()).into(binding.profileImg)
            }
        }
    }

    private fun processImageUri(uri: Uri): String {
        return ImageUtils.convertImageUriToBase64(this, uri, 300, 300) // Adjust max width and height as needed
    }

}