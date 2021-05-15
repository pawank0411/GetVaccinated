package com.vaccine.slot.notifier.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.vaccine.slot.notifier.data.model.DistrictState
import com.vaccine.slot.notifier.databinding.ItemBottomSheetBinding

class BottomSheetAdapter(
        private val mContext: Context,
        private val items: List<DistrictState>,
        private val onClickListener: OnItemClickListener?
) : RecyclerView.Adapter<BottomSheetAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemBottomSheetBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.stateDistrictName.text = items[position].name
        holder.mainLayout.setOnClickListener {
            onClickListener?.onClick(
                    holder.stateDistrictName.toString()
            )
        }
    }

    override fun getItemCount(): Int = items.size

    class MenuViewHolder(binding: ItemBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
        val stateDistrictName: MaterialTextView = binding.stateDistrictName
        val mainLayout: ConstraintLayout = binding.mainLayout
    }

    interface OnItemClickListener {
        fun onClick(
                name: String
        )
    }
}