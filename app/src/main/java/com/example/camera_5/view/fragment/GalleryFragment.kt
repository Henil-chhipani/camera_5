package com.example.camera_5.view.fragment

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.camera_5.databinding.FragmentGalleryBinding
import com.example.camera_5.view.Adapter.GalleryAdapter

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var imageAdapter: GalleryAdapter
    private var imageUris: MutableList<Uri> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ImageUri", "onViewCreated: ${imageUris.size}")
        imageAdapter = GalleryAdapter(requireContext(), imageUris)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerView.adapter = imageAdapter


    }

    override fun onResume() {
        super.onResume()
        refreshImages()
    }

    private fun refreshImages() {
        var size = imageUris.size
        imageUris.clear()
        imageAdapter.notifyItemRangeRemoved(0, size)
        loadImagesFromCamera5Folder(requireContext())
        imageAdapter.refreshItems()
    }


    private fun loadImagesFromCamera5Folder(context: Context): List<Uri> {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_PICTURES}/camera5%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                imageUris.add(contentUri)
            }
        } ?: Log.e("GalleryFragment", "Failed to query images")
        return imageUris
    }
}