package com.codepath.articlesearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterEntryAdapter(private var entries: List<WaterEntry>) :
    RecyclerView.Adapter<WaterEntryAdapter.WaterEntryViewHolder>() {

    class WaterEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.waterAmountTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.waterDateTextView)

        fun bind(entry: WaterEntry) {
            amountTextView.text = "${entry.amount} ml"
            dateTextView.text = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                .format(Date(entry.date))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_entry, parent, false)
        return WaterEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaterEntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    fun updateEntries(newEntries: List<WaterEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}