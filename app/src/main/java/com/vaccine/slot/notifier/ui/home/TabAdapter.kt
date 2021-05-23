package com.vaccine.slot.notifier.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vaccine.slot.notifier.ui.home.fragment.SearchByDistrictFragment
import com.vaccine.slot.notifier.ui.home.fragment.SearchByPinFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 ->
                return SearchByDistrictFragment()
            1 ->
                return SearchByPinFragment()
        }
        return SearchByDistrictFragment()
    }

}