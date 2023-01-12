package com.example.librarybrowse.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.librarybrowse.LibraryBrowseApplication
import com.example.librarybrowse.databinding.FragmentBrowseDetailBinding
import com.example.librarybrowse.ui.viewmodel.SharedViewModel
import com.example.librarybrowse.ui.viewmodel.SharedViewModelFactory

class BrowseDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (activity?.application as LibraryBrowseApplication).database.libraryDao(),
            (activity?.application as LibraryBrowseApplication).database.bookDao()
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

            // Give the binding access to the SharedViewModel
            viewModel = sharedViewModel

            // Add this variable to the binding if fragment specific methods must be passed
            //browseDetailFragment = this@BrowseDetailFragment
        }
        // Return a reference to the root view of the layout
        return binding.root
    }

}