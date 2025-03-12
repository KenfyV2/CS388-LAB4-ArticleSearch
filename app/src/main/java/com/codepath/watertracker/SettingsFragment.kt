package com.codepath.watertracker

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsFragment : Fragment() {
    private val PREF_NAME = "water_tracker_prefs"
    private val PREF_WATER_GOAL = "water_goal"
    private val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private val PREF_REMINDER_HOUR = "reminder_hour"
    private val PREF_REMINDER_MINUTE = "reminder_minute"

    private lateinit var notificationSwitch: Switch
    private lateinit var timePickerButton: Button
    private lateinit var waterGoalEditText: TextInputEditText
    private lateinit var saveGoalButton: Button

    private var reminderHour: Int = 8 // Default to 8 AM
    private var reminderMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        notificationSwitch = view.findViewById(R.id.notificationSwitch)
        timePickerButton = view.findViewById(R.id.timePickerButton)
        waterGoalEditText = view.findViewById(R.id.waterGoalEditText)
        saveGoalButton = view.findViewById(R.id.saveGoalButton)

        // Load saved settings
        loadSettings()

        // Setup listeners
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            timePickerButton.isEnabled = isChecked

            if (isChecked) {
                // Schedule notification with saved time
                NotificationHelper.scheduleDailyNotification(requireContext(), reminderHour, reminderMinute)
                updateTimeButtonText()
            } else {
                // Cancel notifications
                NotificationHelper.cancelNotifications(requireContext())
                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }

            // Save notification preferences
            val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_NOTIFICATIONS_ENABLED, isChecked).apply()
        }

        timePickerButton.setOnClickListener {
            showTimePickerDialog()
        }

        saveGoalButton.setOnClickListener {
            saveWaterGoal()
        }

        // Update time button text
        updateTimeButtonText()
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Load water goal
        val waterGoal = prefs.getInt(PREF_WATER_GOAL, 2000) // Default to 2000ml
        waterGoalEditText.setText(waterGoal.toString())

        // Load notification settings
        val notificationsEnabled = prefs.getBoolean(PREF_NOTIFICATIONS_ENABLED, false)
        reminderHour = prefs.getInt(PREF_REMINDER_HOUR, 8)
        reminderMinute = prefs.getInt(PREF_REMINDER_MINUTE, 0)

        notificationSwitch.isChecked = notificationsEnabled
        timePickerButton.isEnabled = notificationsEnabled
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                reminderHour = hourOfDay
                reminderMinute = minute

                // Save time preferences
                val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                with(prefs.edit()) {
                    putInt(PREF_REMINDER_HOUR, reminderHour)
                    putInt(PREF_REMINDER_MINUTE, reminderMinute)
                    apply()
                }

                // Update notification schedule
                NotificationHelper.scheduleDailyNotification(requireContext(), reminderHour, reminderMinute)

                // Update button text
                updateTimeButtonText()

                Toast.makeText(context, "Reminder time updated", Toast.LENGTH_SHORT).show()
            },
            reminderHour,
            reminderMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun saveWaterGoal() {
        val goalText = waterGoalEditText.text.toString()
        if (goalText.isNotEmpty()) {
            try {
                val goal = goalText.toInt()
                if (goal > 0) {
                    // Save the goal
                    val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putInt(PREF_WATER_GOAL, goal).apply()

                    Toast.makeText(context, "Water goal updated to ${goal}ml", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter a valid goal", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please enter your water goal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeButtonText() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminderHour)
            set(Calendar.MINUTE, reminderMinute)
        }

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        timePickerButton.text = "Reminder Time: ${timeFormat.format(calendar.time)}"
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}