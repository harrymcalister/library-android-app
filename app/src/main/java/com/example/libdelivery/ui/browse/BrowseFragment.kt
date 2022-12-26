package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.libdelivery.databinding.FragmentBrowseBinding

class BrowseFragment : Fragment() {

    private val viewModel: BrowseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout and store reference to the Data Binding
        val binding = FragmentBrowseBinding.inflate(inflater)

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = this@BrowseFragment

            // Give the binding access to the OverviewViewModel
            browseViewModel = viewModel

            // Add this variable to the binding if fragment specific methods must be passed
            // browseFragment = this@BrowseFragment
        }

        // Return a reference to the root view of the layout
        return binding.root
    }
}