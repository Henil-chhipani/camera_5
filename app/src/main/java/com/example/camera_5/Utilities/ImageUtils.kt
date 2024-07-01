package com.example.camera_5.Utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
object ImageUtils {
        // Resize image to reduce processing time and memory usage
        fun resizeImage(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Calculate the scaling factor
            val scaleFactor = Math.min(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            )

            val width = (originalBitmap.width * scaleFactor).toInt()
            val height = (originalBitmap.height * scaleFactor).toInt()

            return Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        }

        // Convert bitmap to Base64 string
        fun convertBitmapToBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        }

        // Convert URI to Base64 string with resizing
        fun convertImageUriToBase64(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): String {
            val resizedBitmap = resizeImage(context, uri, maxWidth, maxHeight)
            return convertBitmapToBase64(resizedBitmap)
        }
    }
