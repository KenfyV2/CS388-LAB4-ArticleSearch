package com.codepath.watertracker

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.watertracker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var waterEntryDao: WaterEntryDao
    private lateinit var waterEntryAdapter: WaterEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        // Set up the bottom navigation with the nav controller
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize database and DAO
        val database = WaterTrackingDatabase.getDatabase(this)
        waterEntryDao = database.waterEntryDao()

        // Setup RecyclerView
        waterEntryAdapter = WaterEntryAdapter(emptyList()) { entry ->
            deleteWaterEntry(entry)
        }


    }

    private fun deleteWaterEntry(entry: WaterEntry) {
        lifecycleScope.launch(Dispatchers.IO) {
            waterEntryDao.delete(entry)
        }
    }

}