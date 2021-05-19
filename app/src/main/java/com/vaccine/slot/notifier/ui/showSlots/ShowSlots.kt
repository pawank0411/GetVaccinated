package com.vaccine.slot.notifier.ui.showSlots

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.HorizontalGridSpan7Model_
import com.vaccine.slot.notifier.ItemLayoutCenterHeaderBindingModel_
import com.vaccine.slot.notifier.ItemLayoutSlotsBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding

class ShowSlots : AppCompatActivity() {

    private lateinit var activityShowSlotsBinding: ActivityShowSlotsBinding
    private lateinit var viewModel: ShowSlotsViewModel
    private lateinit var slotDateAdapter: SlotDateAdapter

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

        val intent = Intent().extras // DATA FROM INTENT

        viewModel = ViewModelProvider(this).get(ShowSlotsViewModel::class.java)

        viewModel.getSlotDetailsDistrictWise(502)

        viewModel.toast.observe(this, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        slotDateAdapter = SlotDateAdapter(this, viewModel.getSevenDayDate())
        activityShowSlotsBinding.datesList.adapter = slotDateAdapter
        activityShowSlotsBinding.datesList.layoutManager =
                GridLayoutManager(this, 7, LinearLayoutManager.VERTICAL, false)

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = viewModel.slotDetailsDistrict.value
                if (dataList != null) {
                    for (i in dataList) {
                        // This is a header..
                        ItemLayoutCenterHeaderBindingModel_()
                                .id(i.pincode)
                                .centerName(i.center)
                                .pinCode(i.pincode.toString() + " \u2022 " + i.address)
                                .price(i.fee)
                                .addTo(controller)

                        // This is a inner list which shows vaccines nos
                        val subItems = mutableListOf<EpoxyModel<*>>()
                        for (j in 1..7) {
                            subItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                            .id(i.pincode)
                                            .vaccineName(i.vaccine)
                                            .vaccineNo(i.availableCapacity.toString())
                            )
                        }

                        HorizontalGridSpan7Model_()
                                .id(subItems.hashCode())
                                .models(subItems)
                                .addTo(controller)
                    }
                }
            }
            // for binding
            /*
            .onBind { _, view, _ ->
             val binding = view.dataBinding as ViewholderItemLayoutCenterHeaderBinding
             }
             */
        })

        viewModel.slotDetailsDistrict.observe(this, {
            Log.d("ObservedResponse", it.toString())
            activityShowSlotsBinding.epoxy.requestModelBuild()
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