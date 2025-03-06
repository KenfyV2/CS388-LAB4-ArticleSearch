package com.codepath.watertracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.watertracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var waterEntryDao: WaterEntryDao
    private lateinit var waterEntryAdapter: WaterEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database and DAO
        val database = WaterTrackingDatabase.getDatabase(this)
        waterEntryDao = database.waterEntryDao()

        // Setup RecyclerView
        waterEntryAdapter = WaterEntryAdapter(emptyList())
        binding.waterEntriesRecyclerView.apply {
            adapter = waterEntryAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Observe database entries
        lifecycleScope.launch {
            waterEntryDao.getAllEntries().collect { entries ->
                waterEntryAdapter.updateEntries(entries)
            }
        }

        // Add new entry button
        binding.addEntryButton.setOnClickListener {
            startActivity(Intent(this, AddEntryActivity::class.java))
        }
    }
}