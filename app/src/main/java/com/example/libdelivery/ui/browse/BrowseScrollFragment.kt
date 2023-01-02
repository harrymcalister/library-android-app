package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.R
import com.example.libdelivery.database.book.BookWithLibName
import com.example.libdelivery.databinding.FragmentBrowseScrollBinding
import kotlinx.coroutines.launch

class BrowseScrollFragment : Fragment() {

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
        val binding = FragmentBrowseScrollBinding.inflate(inflater)

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the BrowseViewModel
            browseViewModel = viewModel

            // Set the recycler view adapter
            val bookAdapter = BookAdapter(BookListener { book: BookWithLibName ->
                onBookClicked(book)
                findNavController()
                    .navigate(R.id.action_navigation_browse_scroll_to_navigation_browse_detail)

            })
            browseScrollRecyclerView.adapter = bookAdapter

            // submitList() is a call that accesses the database. To prevent the
            // call from potentially locking the UI, use a coroutine.
            lifecycle.coroutineScope.launch {
                viewModel.allBooksWithLibName().collect() {
                    bookAdapter.submitList(it)
                }
            }
        }
        // Return a reference to the root view of the layout
        return binding.root
    }

    fun onBookClicked(book: BookWithLibName) {
        viewModel.setSelectedBook(book)
    }
}