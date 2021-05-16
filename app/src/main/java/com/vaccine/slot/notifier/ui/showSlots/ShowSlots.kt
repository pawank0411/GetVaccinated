package com.vaccine.slot.notifier.ui.showSlots

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding

class ShowSlots : AppCompatActivity() {

    private lateinit var activityShowSlotsBinding: ActivityShowSlotsBinding
    private lateinit var viewModel: ShowSlotsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityShowSlotsBinding = ActivityShowSlotsBinding.inflate(layoutInflater)
        setContentView(activityShowSlotsBinding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
            title = "COVID-19 Vaccine Availability"
        }

        viewModel = ViewModelProviders.of(this).get(ShowSlotsViewModel::class.java)
        viewModel.getSlotDetailsDistrictWise(502)
        viewModel.slotDetailsDistrict.observe(this, {
            Log.d("ObservedResponse", it.toString())
        })

        viewModel.toast.observe(this, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            finish()
        }
        return true
    }
}