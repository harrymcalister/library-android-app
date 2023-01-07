package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.databinding.adapters.SearchViewBindingAdapter.setOnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.R
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.databinding.FragmentBrowseScrollBinding
import com.example.libdelivery.ui.bindRecyclerView
import com.example.libdelivery.ui.viewmodel.SharedViewModel
import com.example.libdelivery.ui.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseScrollFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
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

        val bookAdapter = BookAdapter(sharedViewModel, BookListener { book: BookWithLibDetails, distString: String ->
            onBookClicked(book, distString)
            findNavController()
                .navigate(R.id.action_navigation_browse_scroll_to_navigation_browse_detail)

        })

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the SharedViewModel
            viewModel = sharedViewModel

            // Set the recycler view adapter
            browseScrollRecyclerView.adapter = bookAdapter

            // MenuProvider requires reference to binding to show 'no results' text
            setMenuProvider(this)
        }

        // Return a reference to the root view of the layout
        return binding.root
    }

    fun onBookClicked(book: BookWithLibDetails, distString: String) {
        sharedViewModel.setSelectedBook(book, distString)
    }

    // Requires reference to RecyclerView to submit search results using binding adapter
    private fun setMenuProvider(fragmentBinding: FragmentBrowseScrollBinding) {

        val menuHost = requireActivity()

        menuHost.addMenuProvider(object: MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar_browse, menu)

                val searchView = menu.findItem(R.id.search_button).actionView as SearchView
                searchView.queryHint = "Search by title or author..."
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                // Store num results so can potentially show 'no results' text
                                val numResults = lifecycleScope.async {
                                    // Method will give updated book list to view model
                                    sharedViewModel.performDatabaseQuery {
                                        sharedViewModel.searchBooksWithKeyword(query)
                                    }
                                }
                                // Make 'no results' text visible if necessary
                                withContext(Dispatchers.Main) {
                                    if (numResults.await() == 0) {
                                        fragmentBinding.noResultsText.visibility = View.VISIBLE
                                    } else {
                                        fragmentBinding.noResultsText.visibility = View.GONE
                                    }
                                }
                            }
                        }
                        // Close keyboard after search submitted
                        searchView.clearFocus();
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.search_button -> {
                        true
                    }
                    R.id.filter_button -> {
                        // filter()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}