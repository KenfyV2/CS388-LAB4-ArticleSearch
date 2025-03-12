package com.codepath.watertracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private lateinit var weeklyChart: BarChart
    private lateinit var averageIntakeValue: TextView
    private lateinit var totalEntriesValue: TextView
    private lateinit var totalConsumedValue: TextView
    private lateinit var weeklyConsumptionValue: TextView
    private lateinit var todayConsumptionValue: TextView
    private lateinit var lastEntryValue: TextView
    private lateinit var noDataText: TextView
    private lateinit var statsContainer: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weeklyChart = view.findViewById(R.id.weeklyChart)
        averageIntakeValue = view.findViewById(R.id.averageIntakeValue)
        totalEntriesValue = view.findViewById(R.id.totalEntriesValue)
        totalConsumedValue = view.findViewById(R.id.totalConsumedValue)
        weeklyConsumptionValue = view.findViewById(R.id.weeklyConsumptionValue)
        todayConsumptionValue = view.findViewById(R.id.todayConsumptionValue)
        lastEntryValue = view.findViewById(R.id.lastEntryValue)
        noDataText = view.findViewById(R.id.noDataText)
        statsContainer = view.findViewById(R.id.statsContainer)

        loadData()
    }

    private fun loadData() {
        val entries = getSampleData()

        if (entries.isEmpty()) {
            noDataText.visibility = View.VISIBLE
            statsContainer.visibility = View.GONE
            weeklyChart.visibility = View.GONE
            return
        }

        noDataText.visibility = View.GONE
        statsContainer.visibility = View.VISIBLE
        weeklyChart.visibility = View.VISIBLE

        setupWeeklyChart(entries)
        updateStatistics(entries)
    }

    private fun setupWeeklyChart(entries: List<WaterEntry>) {
        val barEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dailyTotals = FloatArray(7)

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, -6)
        }

        for (i in 0..6) {
            labels.add(dayFormat.format(calendar.time))
            val currentDate = calendar.time

            entries.forEach { entry ->
                if (isSameDay(entry.timestamp, currentDate)) {
                    dailyTotals[i] += entry.amount.toFloat()
                }
            }
            barEntries.add(BarEntry(i.toFloat(), dailyTotals[i]))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val dataSet = BarDataSet(barEntries, "Daily Water Intake (ml)").apply {
            color = resources.getColor(R.color.water_medium, null)
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        weeklyChart.apply {
            data = BarData(dataSet)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            animateY(1000)
            invalidate()
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun updateStatistics(entries: List<WaterEntry>) {
        val totalEntries = entries.size
        var totalConsumed = 0
        var weeklyConsumption = 0
        var todayConsumption = 0
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekStart = calendar.time
        val lastEntry = entries.last()

        entries.forEach { entry ->
            totalConsumed += entry.amount
            if (entry.timestamp.after(weekStart)) weeklyConsumption += entry.amount
            if (isSameDay(entry.timestamp, today)) todayConsumption += entry.amount
        }

        val averageIntake = if (totalEntries > 0) totalConsumed / totalEntries else 0

        averageIntakeValue.text = "$averageIntake ml"
        totalEntriesValue.text = totalEntries.toString()
        totalConsumedValue.text = "$totalConsumed ml"
        weeklyConsumptionValue.text = "$weeklyConsumption ml"
        todayConsumptionValue.text = "$todayConsumption ml"
        lastEntryValue.text = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(lastEntry.timestamp)
    }

    private fun getSampleData(): List<WaterEntry> {
        val entries = mutableListOf<WaterEntry>()
        val calendar = Calendar.getInstance()

        repeat(10) {
            repeat(2 + Random.nextInt(3)) {
                calendar.set(Calendar.HOUR_OF_DAY, 8 + Random.nextInt(12))
                calendar.set(Calendar.MINUTE, Random.nextInt(60))
                val amount = 150 + Random.nextInt(201)
                entries.add(WaterEntry(amount, calendar.time))
            }
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return entries
    }

    data class WaterEntry(val amount: Int, val timestamp: Date)
}
