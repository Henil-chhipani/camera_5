package com.example.camera_5.view.fragment

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.camera_5.R
import com.example.camera_5.databinding.FragmentCameraBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private var cameraProvider: ProcessCameraProvider? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val interval = arrayOf(1, 2, 3, 4, 5)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            interval
        )
        binding.intervalInput.adapter = adapter

        binding.takepicBtn.setOnClickListener {
            val internal = binding.intervalInput.selectedItem.toString().toInt()
            takePictures(internal)
        }

        if (allPermissionsGranted()) {
            Log.d("permission", "onViewCreated: called")
            startCamera()
        } else {
            Log.d("permission", "onViewCreated: failded")
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onResume() {
        super.onResume()
            startCamera()
        }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {

        Log.d("camera123", "startCamera: ")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.preView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePictures(interval: Int) {
        for (i in 1..interval) {
            takePicture()
        }
    }

    private fun takePicture() {
        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/camera5")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exception.message}", exception)
                    Snackbar.make(
                        binding.iconsLayout,
                        "Photo capture failed: ${exception.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(
                        "CameraFragment",
                        "Photo capture succeeded: ${outputFileResults.savedUri}"
                    )
                    Snackbar.make(
                        binding.iconsLayout,
                        "Photo saved successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
