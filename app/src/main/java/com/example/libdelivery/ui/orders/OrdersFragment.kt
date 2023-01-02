package com.example.libdelivery.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.libdelivery.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout and store reference to the Data Binding
        val binding = FragmentOrdersBinding.inflate(inflater)

        // This step is sometimes completed in onViewCreated() alongside a local _binding variable
        binding.apply {
            // Allow Data Binding to observe LiveData with the lifecycle of this Fragment
            lifecycleOwner = viewLifecycleOwner

            // Give the binding access to the OverviewViewModel
            ordersViewModel = viewModel

            // Add this variable to the binding if fragment specific methods must be passed
            // ordersFragment = this@OrdersFragment
        }

        // Return a reference to the root view of the layout
        return binding.root
    }
}