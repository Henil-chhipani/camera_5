package com.example.camera_5.view.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.camera_5.view.fragment.CameraFragment
import com.example.camera_5.view.fragment.GalleryFragment
import com.example.camera_5.view.fragment.ProfileFragment


class BottomNavAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    // Define the total number of fragments
    override fun getItemCount(): Int = 3

    // Create and return the appropriate fragment based on the position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment() // profile Fragment
            1 -> CameraFragment() // profile Fragment
            2 -> GalleryFragment() // Gallery Fragment
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
