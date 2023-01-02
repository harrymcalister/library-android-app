package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.databinding.FragmentBrowseDetailBinding
import kotlinx.coroutines.launch

class BrowseDetailFragment : Fragment() {

    private val viewModel: BrowseViewModel by activityViewModels {
        BrowseViewModelFactory(
            (activity?.application as LibDeliveryApplication).database.libraryDao(),
            (activity?.application as LibDeliveryApplication).database.bookDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout and store reference to the Data Binding
        val binding = FragmentBrowseDetailBinding.inflate(inflater)

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the BrowseViewModel
            browseViewModel = viewModel

            // Add this variable to the binding if fragment specific methods must be passed
            //browseDetailFragment = this@BrowseDetailFragment
        }
        // Return a reference to the root view of the layout
        return binding.root
    }
}