package com.vaccine.slot.notifier.ui.showSlots

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.vaccine.slot.notifier.data.model.Date
import com.vaccine.slot.notifier.databinding.ItemDateListBinding

class SlotDateAdapter(
        private val mContext: Context,
        private val items: List<Date>
) : RecyclerView.Adapter<SlotDateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDateListBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val splitDate = items[position].date.split(" ")
        val displayDate = splitDate[0] + "\n" + " " + splitDate[1] + " " + splitDate[2]
        holder.dateText.text = displayDate
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(binding: ItemDateListBinding) : RecyclerView.ViewHolder(binding.root) {
        val dateText: MaterialButton = binding.dateText
    }
}