package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.MenuItem.OnActionExpandListener
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.R
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.databinding.FragmentBrowseScrollBinding
import com.example.libdelivery.ui.viewmodel.SharedViewModel
import com.example.libdelivery.ui.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            setMenuProvider()
        }

        // Return a reference to the root view of the layout
        return binding.root
    }

    fun onBookClicked(book: BookWithLibDetails, distString: String) {
        sharedViewModel.setSelectedBook(book, distString)
    }

    private fun setMenuProvider() {
        val menuHost = requireActivity()

        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar_browse, menu)

                val searchMenuItem = menu.findItem(R.id.search_button)
                val searchView = searchMenuItem.actionView as SearchView


                // This custom listener is needed to write previous queries back in the SearchView
                searchMenuItem.setOnActionExpandListener(object : OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                        // Expand the SearchView first to enter last query
                        searchView.onActionViewExpanded()
                        searchView.setQuery(sharedViewModel.lastQuery.value, false)
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                        searchView.onActionViewCollapsed()
                        return true
                    }
                })

                searchView.setIconifiedByDefault(true)
                searchView.isFocusable = true
                searchView.requestFocusFromTouch()

                searchView.queryHint = "Search by title or author..."
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        // Close keyboard when enter is pressed
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!isVisible || searchView.isIconified) {
                            // The fragment was exited if not visible so ignore
                            // The fragment was just opened if iconified so ignore
                            return true
                        }
                        if (newText != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                // Method will pass updated book list to view model
                                sharedViewModel.performDatabaseQuery(
                                    daoQuery = {
                                        if (newText != "") {
                                            sharedViewModel.searchBooksWithKeyword(newText)
                                        } else {
                                            sharedViewModel.allBooksWithLibName()
                                        }
                                    }
                                )
                            }
                            // Store lastQuery in viewModel so if fragment destroyed same results appear
                            sharedViewModel.setLastQuery(newText)
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.filter_button -> {
                        Log.e("BrowseScrollFragment", "filter button pressed")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}

