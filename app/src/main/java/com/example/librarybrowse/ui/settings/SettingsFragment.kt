package com.example.librarybrowse.ui.settings

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.librarybrowse.LibraryBrowseApplication
import com.example.librarybrowse.R
import com.example.librarybrowse.databinding.FragmentSettingsBinding
import com.example.librarybrowse.ui.viewmodel.SharedViewModel
import com.example.librarybrowse.ui.viewmodel.SharedViewModelFactory

class SettingsFragment : Fragment() {

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
        val binding = FragmentSettingsBinding.inflate(inflater)

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the SharedViewModel
            viewModel = sharedViewModel

            // Add this variable to the binding if fragment specific methods must be passed
            // settingsFragment = this@SettingsFragment
        }

        setMenuProvider()

        // Return a reference to the root view of the layout
        return binding.root
    }

    private fun setMenuProvider() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar_settings, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.feedback_button -> {
                        // feedback()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}