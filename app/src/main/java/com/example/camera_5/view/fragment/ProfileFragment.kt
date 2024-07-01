package com.example.camera_5.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.camera_5.LoginActivity
import com.example.camera_5.R
import com.example.camera_5.SignUpActivity
import com.example.camera_5.Utilities.ImageUtils
import com.example.camera_5.data.AuthViewModelFactory
import com.example.camera_5.data.model.UserModel
import com.example.camera_5.data.repository.UserRepository
import com.example.camera_5.databinding.FragmentProfileBinding
import com.example.camera_5.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private var selectedImageUri: Uri? = null
    var image: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nameTxtLayout.setEndIconOnClickListener {
            binding.nameTxtInput.isEnabled = true

        }

        val userRepository = UserRepository()
        val factory = AuthViewModelFactory(userRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        lateinit var uid: String


        binding.profileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SignUpActivity.REQUEST_CODE_IMAGE_PICK)


        }


        binding.updateBtn.setOnClickListener {
            val name = binding.nameTxtInput.text.toString().trim()
            val email = binding.emailTxtInput.text.toString().trim()
            binding.lodingIcon.visibility = View.VISIBLE

            authViewModel.getCurrentUser()?.let { user ->
                val updatedUser = UserModel(uid, name, email, image.toString())
                authViewModel.updateUserProfile(updatedUser) { success, message ->
                    if (success) {
                        binding.lodingIcon.visibility = View.GONE
                        binding.nameTxtInput.isEnabled = false
                        Snackbar.make(binding.updateBtn, "Profile updated", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        Snackbar.make(
                            binding.updateBtn,
                            message ?: "Profile update failed",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        authViewModel.userProfile.observe(viewLifecycleOwner, Observer { userProfile ->
            userProfile?.let {
                binding.nameTxtInput.setText(it.name.toString())
                binding.emailTxtInput.setText(it.email.toString())
                uid = userProfile.uid
                image = userProfile.image
                if (userProfile.image.isNotEmpty()) {
                    val decodedImage =
                        android.util.Base64.decode(userProfile.image, android.util.Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
//                    binding.profileImg.setImageBitmap(bitmap)

                    Glide.with(this).load(bitmap).transform(CircleCrop()).into(binding.profileImg)
                }
            }
        })

        binding.logoutBtn.setOnClickListener {
            authViewModel.logout()
        }

        authViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user == null) {
                // User is logged out, navigate to LoginActivity
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == SignUpActivity.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).transform(CircleCrop()).into(binding.profileImg)
                image = processImageUri(uri)
            }
        }
    }

    private fun processImageUri(uri: Uri): String {
        return ImageUtils.convertImageUriToBase64(requireContext(), uri, 300, 300) // Adjust max width and height as needed
    }
}