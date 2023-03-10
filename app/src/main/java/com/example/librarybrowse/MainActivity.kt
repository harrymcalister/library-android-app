package com.example.librarybrowse

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.librarybrowse.databinding.ActivityMainBinding
import com.example.librarybrowse.utils.location.LocationService
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions used by location service
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0,
        )

        // Inflate layout and store reference to the Data Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)

        // Set lifecycle owner of MainActivity
        binding.lifecycleOwner = this

        // Fill the window with the content provided by the layout
        setContentView(binding.root)

        setupBottomNavBar(binding)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupBottomNavBar(binding: ActivityMainBinding) {
        val navView: BottomNavigationView = binding.bottomNavView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        navController = navHostFragment.navController

        // Pass each menu ID as a set of Ids
        val bottomNavBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_browse_scroll, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, bottomNavBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        // Starting intent for location service so it saves battery when app is not
        // in use (stop intent is in onPause)
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    override fun onPause() {
        super.onPause()
        // Stopping intent for location service so it saves battery when app is not
        // in use  (start intent is in onResume)
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            // startService is still used even though we are stopping the service
            startService(this)
        }
    }
}