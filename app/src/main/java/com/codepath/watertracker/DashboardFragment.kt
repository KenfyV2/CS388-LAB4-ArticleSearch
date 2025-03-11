package com.codepath.watertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.codepath.watertracker.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var waterEntryDao: WaterEntryDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database and DAO
        val database = WaterTrackingDatabase.getDatabase(requireContext())
        waterEntryDao = database.waterEntryDao()

        // Load statistics
        loadStatistics()
    }

    private fun loadStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            waterEntryDao.getAllEntries().collect { entries ->
                if (entries.isEmpty()) {
                    binding.noDataText.visibility = View.VISIBLE
                    binding.statsContainer.visibility = View.GONE
                } else {
                    binding.noDataText.visibility = View.GONE
                    binding.statsContainer.visibility = View.VISIBLE

                    // Calculate stats
                    calculateAndDisplayStats(entries)
                }
            }
        }
    }

    private fun calculateAndDisplayStats(entries: List<WaterEntry>) {
        // Get average intake
        viewLifecycleOwner.lifecycleScope.launch {
            val averageIntake = waterEntryDao.getAverageWaterIntake()
            binding.averageIntakeValue.text = "${averageIntake.toInt()} ml"
        }

        // Total entries
        binding.totalEntriesValue.text = entries.size.toString()

        // Total water consumed
        val totalWater = entries.sumOf { it.amount }
        binding.totalConsumedValue.text = "$totalWater ml"

        // This week's consumption
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.timeInMillis

        val thisWeekEntries = entries.filter { it.date >= startOfWeek }
        val thisWeekConsumption = thisWeekEntries.sumOf { it.amount }
        binding.weeklyConsumptionValue.text = "$thisWeekConsumption ml"

        // Today's consumption
        val todayMillis = System.currentTimeMillis()
        val startOfDay = todayMillis - (todayMillis % (24 * 60 * 60 * 1000))
        val todayEntries = entries.filter { it.date >= startOfDay }
        val todayConsumption = todayEntries.sumOf { it.amount }
        binding.todayConsumptionValue.text = "$todayConsumption ml"

        // Last entry date
        if (entries.isNotEmpty()) {
            val lastDate = Date(entries[0].date)
            val formatter = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
            binding.lastEntryValue.text = formatter.format(lastDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}