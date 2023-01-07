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
import androidx.navigation.fragment.findNavController
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.R
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.databinding.FragmentBrowseScrollBinding
import com.example.libdelivery.ui.viewmodel.SharedViewModel
import com.example.libdelivery.ui.viewmodel.SharedViewModelFactory

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

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the SharedViewModel
            viewModel = sharedViewModel

            // Set the recycler view adapter
            // This is performed here instead of layout file with data binding
            // to simplify the coroutine launch below
            val bookAdapter = BookAdapter(sharedViewModel, BookListener { book: BookWithLibDetails, distString: String ->
                onBookClicked(book, distString)
                findNavController()
                    .navigate(R.id.action_navigation_browse_scroll_to_navigation_browse_detail)

            })
            browseScrollRecyclerView.adapter = bookAdapter

            browseScrollRecyclerView
        }

        



        // Return a reference to the root view of the layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //https://stackoverflow.com/questions/30721664/android-toolbar-adding-menu-items-for-different-fragments
        //https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
        //https://www.youtube.com/watch?v=8q-4AJFlraI&ab_channel=Indently
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar_browse, menu)

                val searchView = menu.findItem(R.id.search_button).actionView as SearchView
                searchView.queryHint = "Search by title or author..."
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.e("x", "text submit")
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.e("x", "text changed")
                        return true
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

    fun onBookClicked(book: BookWithLibDetails, distString: String) {
        sharedViewModel.setSelectedBook(book, distString)
    }
}