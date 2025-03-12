package com.codepath.watertracker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.watertracker.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var waterEntryDao: WaterEntryDao
    private lateinit var waterEntryAdapter: WaterEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database and DAO
        val database = WaterTrackingDatabase.getDatabase(requireContext())
        waterEntryDao = database.waterEntryDao()

        // Setup RecyclerView
        waterEntryAdapter = WaterEntryAdapter(emptyList()) { entry ->
            deleteWaterEntry(entry)
        }

        // Set layout manager based on orientation
        setRecyclerViewLayoutManager(resources.configuration.orientation)

        // Observe database entries
        viewLifecycleOwner.lifecycleScope.launch {
            waterEntryDao.getAllEntries().collect { entries ->
                waterEntryAdapter.updateEntries(entries)
                updateGoalProgress(entries)
            }
        }

        // Add new entry button
        binding.addEntryButton.setOnClickListener {
            startActivity(Intent(requireContext(), AddEntryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            waterEntryDao.getAllEntries().collect { entries ->
                updateGoalProgress(entries)
            }
        }
    }
    private fun deleteWaterEntry(entry: WaterEntry) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            waterEntryDao.delete(entry)
        }
    }

    private fun updateGoalProgress(entries: List<WaterEntry>) {
        val prefs = requireContext().getSharedPreferences("water_tracker_prefs", Context.MODE_PRIVATE)
        val dailyGoal = prefs.getInt("water_goal", 2000) // Get saved goal or default to 2000ml

        // Calculate today's water intake
        val todayMillis = System.currentTimeMillis()
        val startOfDay = todayMillis - (todayMillis % (24 * 60 * 60 * 1000))
        val todayEntries = entries.filter { it.date >= startOfDay }
        val todayAmount = todayEntries.sumOf { it.amount }

        // Update progress
        val progress = (todayAmount.toFloat() / dailyGoal) * 100
        binding.waterProgressBar.progress = progress.toInt()
        binding.waterAmountText.text = "$todayAmount ml / $dailyGoal ml"
    }


    private fun setRecyclerViewLayoutManager(orientation: Int) {
        binding.waterEntriesRecyclerView.apply {
            adapter = waterEntryAdapter

            layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Use GridLayoutManager in landscape
                GridLayoutManager(requireContext(), 2)
            } else {
                // Use LinearLayoutManager in portrait
                LinearLayoutManager(requireContext())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager(newConfig.orientation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}