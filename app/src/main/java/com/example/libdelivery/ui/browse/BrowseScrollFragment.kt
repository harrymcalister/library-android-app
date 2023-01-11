package com.example.libdelivery.ui.browse

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.MenuItem.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat.setOnActionExpandListener
import androidx.core.view.MenuProvider
import androidx.core.view.iterator
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
//                        searchView.setQuery(sharedViewModel.lastQuery.value, false)
                        return true
                    }
                })

                configureSearchView(searchView)

                // Expand action view to display last query upon opening fragment
                if (!sharedViewModel.lastQuery.value.isNullOrBlank()) {
                    // Query is set in this line because of custom OnActionExpandListener
                    searchMenuItem.expandActionView()
                    searchView.clearFocus()
                }

                val filterMenuItemSubMenu = menu.findItem(R.id.filter_button).subMenu

                configureFilterSubMenu(filterMenuItemSubMenu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureSearchView(searchView: SearchView) {
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
                if (!isResumed || !isVisible || searchView.isIconified) {
                    // The app was closed or fragment was exited if not resumed so ignore
                    // The fragment was exited if not visible so ignore
                    // The fragment was just opened if iconified so ignore
                    return true
                }
                // newText should only be null when first starting app, in which case
                // no filters have been selected so filter logic not necessary
                if (newText != null) {
                    // Filter variable is null if no subject filter currently selected
                    val currentSubjectFilterString: String? = sharedViewModel.subjectFilter.value

                    val appropriateDaoQuery = sharedViewModel.generateCorrectDaoQuery(
                        newText,
                        currentSubjectFilterString,
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Method will pass updated book list to view model
                        sharedViewModel.performDatabaseQuery(
                            daoQuery = appropriateDaoQuery
                        )
                    }
                    // Store lastQuery in viewModel so if fragment destroyed same results appear
                    sharedViewModel.setLastQuery(newText)
                }
                return true
            }
        })
    }

    private fun configureFilterSubMenu(filterSubMenu: SubMenu) {
        // Check the menu items associated with the currently applied filters in
        // the ViewModel for clarity
        val distanceFilterMenuItem = filterSubMenu.findItem(
            when (sharedViewModel.distanceFilter.value) {
                25 -> R.id.distance_within_25km
                10 -> R.id.distance_within_10km
                3  -> R.id.distance_within_3km
                1 -> R.id.distance_within_1km
                else -> R.id.distance_any
            }
        )
        val subjectFilterMenuItem = filterSubMenu.findItem(
            when (sharedViewModel.subjectFilter.value) {
                "Romance" -> R.id.subject_romance
                "Thriller" -> R.id.subject_thriller
                "Horror" -> R.id.subject_horror
                "Mystery" -> R.id.subject_mystery
                "Fantasy" -> R.id.subject_fantasy
                "History" -> R.id.subject_history
                "Science Fiction" -> R.id.subject_science_fiction
                "Literature" -> R.id.subject_literature
                "Business" -> R.id.subject_business
                "Mathematics" -> R.id.subject_mathematics
                "Science" -> R.id.subject_science
                "Arts" -> R.id.subject_arts
                else -> R.id.subject_all
            }
        )
        distanceFilterMenuItem.isChecked = true
        subjectFilterMenuItem.isChecked = true

        // Applies to every item in filter submenu to allow radio button checking,
        // stop submenu from closing on clicks, and set enabled filters in the ViewModel
        for (menuItem in filterSubMenu) {
            menuItem.setOnMenuItemClickListener(object : OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                    if (menuItem == null) {
                        return false
                    }
                    configureOnMenuItemClick(menuItem)
                    return false
                }
            })
        }
    }

    // Used in the configureFilterSubMenu method to define filter submenu click behaviour
    private fun configureOnMenuItemClick(menuItem: MenuItem) {
        menuItem.apply {
            // Check the button if it is a radio button
            if (isCheckable) {
                isChecked = true
            }
            // Prevent menu from closing when pressing buttons
            setShowAsAction(SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            actionView = View(context)
            setOnActionExpandListener(object : OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return false
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    return false
                }
            })

            // Define unique behaviour for when a filter setting is chosen
            // Also check the currently applied filters in the ViewModel for clarity
            when (itemId) {
                R.id.distance_any -> {
                    Log.e("BrowseScrollFragment", "filter any distance")
                    sharedViewModel.setDistanceFilter(null)
                }
                R.id.distance_within_1km -> {
                    Log.e("BrowseScrollFragment", "filter 1km")
                    sharedViewModel.setDistanceFilter(1)
                }
                R.id.distance_within_3km -> {
                    Log.e("BrowseScrollFragment", "filter 5km")
                    sharedViewModel.setDistanceFilter(3)
                }
                R.id.distance_within_10km -> {
                    Log.e("BrowseScrollFragment", "filter 10km")
                    sharedViewModel.setDistanceFilter(10)
                }
                R.id.distance_within_25km -> {
                    Log.e("BrowseScrollFragment", "filter 25km")
                    sharedViewModel.setDistanceFilter(25)
                }
                R.id.subject_all -> {
                    Log.e("BrowseScrollFragment", "filter no subject")
                    sharedViewModel.setSubjectFilter(null)
                }
                R.id.subject_romance -> {
                    Log.e("BrowseScrollFragment", "filter romance")
                    sharedViewModel.setSubjectFilter("Romance")
                }
                R.id.subject_thriller -> {
                    Log.e("BrowseScrollFragment", "filter thriller")
                    sharedViewModel.setSubjectFilter("Thriller")
                }
                R.id.subject_horror -> {
                    Log.e("BrowseScrollFragment", "filter horror")
                    sharedViewModel.setSubjectFilter("Horror")
                }
                R.id.subject_mystery -> {
                    Log.e("BrowseScrollFragment", "filter mystery")
                    sharedViewModel.setSubjectFilter("Mystery")
                }
                R.id.subject_fantasy -> {
                    Log.e("BrowseScrollFragment", "filter fantasy")
                    sharedViewModel.setSubjectFilter("Fantasy")
                }
                R.id.subject_history -> {
                    Log.e("BrowseScrollFragment", "filter history")
                    sharedViewModel.setSubjectFilter("History")
                }
                R.id.subject_science_fiction -> {
                    Log.e("BrowseScrollFragment", "filter science fiction")
                    sharedViewModel.setSubjectFilter("Science Fiction")
                }
                R.id.subject_literature -> {
                    Log.e("BrowseScrollFragment", "filter literature")
                    sharedViewModel.setSubjectFilter("Literature")
                }
                R.id.subject_business -> {
                    Log.e("BrowseScrollFragment", "filter business")
                    sharedViewModel.setSubjectFilter("Business")
                }
                R.id.subject_mathematics -> {
                    Log.e("BrowseScrollFragment", "filter mathematics")
                    sharedViewModel.setSubjectFilter("Mathematics")
                }
                R.id.subject_science -> {
                    Log.e("BrowseScrollFragment", "filter science")
                    sharedViewModel.setSubjectFilter("Science")
                }
                R.id.subject_arts -> {
                    Log.e("BrowseScrollFragment", "filter arts")
                    sharedViewModel.setSubjectFilter("Arts")
                }
            }
        }
    }
}

