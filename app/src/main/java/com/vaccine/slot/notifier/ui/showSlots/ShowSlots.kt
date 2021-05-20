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
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.HorizontalGridSpan7Model_
import com.vaccine.slot.notifier.ItemLayoutCenterHeaderBindingModel_
import com.vaccine.slot.notifier.ItemLayoutSlotsBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
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

        viewModel.getSlotDetailsDistrictWise(111)

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

                val districtResponse = viewModel.slotDetailsDistrict.value?.data
                districtResponse?.centers?.forEach { center ->
                    // This is a header..
                    ItemLayoutCenterHeaderBindingModel_()
                            .id(center.pincode)
                            .centerName(center.name)
                            .pinCode(center.pincode.toString() + " \u2022 " + center.address)
                            .price(center.feeType)
                            .addTo(controller)

                    // This is a inner list which shows vaccines nos
                    val subItems = mutableListOf<EpoxyModel<*>>()
                    for (i in 0..6) {
                        val currentDate = viewModel.getSevenDayDate()[i].date
                        val sdf2 = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
                        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        var date = sdf2.parse(currentDate)

                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))

                        date = calendar.time

                        val currentDateStr = sdf.format(date)

                        var currentSession: Session? = null
                        center.sessions?.forEach { session ->
                            println("CurrentDate: $currentDateStr")
                            println("SessionDate: ${session.date}")
                            if (session.date!! == currentDateStr) {
                                currentSession = session
                            }
                        }

                        if (currentSession != null) {
                            subItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                            .id(currentSession.hashCode())
                                            .vaccineName(currentSession!!.vaccine)
                                            .vaccineNo(currentSession!!.availableCapacity?.toInt().toString())
                            )
                        } else {
                            subItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                            .id(Random().nextInt())
                                            .vaccineName("")
                                            .vaccineNo("NA")
                            )
                        }
                    }

                    HorizontalGridSpan7Model_()
                            .id(subItems.hashCode())
                            .models(subItems)
                            .padding(
                                    Carousel.Padding(0, 0, 0, 0, 20)
                            )
                            .addTo(controller)
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