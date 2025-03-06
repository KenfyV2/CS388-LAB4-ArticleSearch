package com.codepath.watertracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codepath.watertracker.databinding.ActivityAddEntryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEntryBinding
    private lateinit var waterEntryDao: WaterEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = WaterTrackingDatabase.getDatabase(this)
        waterEntryDao = database.waterEntryDao()

        binding.saveButton.setOnClickListener {
            val waterAmount = binding.waterAmountEditText.text.toString().toIntOrNull()

            if (waterAmount != null && waterAmount > 0) {
                lifecycleScope.launch(Dispatchers.IO) {
                    waterEntryDao.insert(WaterEntry(amount = waterAmount))
                    runOnUiThread {
                        Toast.makeText(this@AddEntryActivity, "Water entry saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid water amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}