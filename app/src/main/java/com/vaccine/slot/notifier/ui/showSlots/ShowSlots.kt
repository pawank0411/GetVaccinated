package com.vaccine.slot.notifier.ui.showSlots

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
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import com.vaccine.slot.notifier.databinding.ViewholderItemLayoutFilterBinding
import com.vaccine.slot.notifier.ui.home.HomeActivity
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

        viewModel = ViewModelProvider(this).get(ShowSlotsViewModel::class.java)
        viewModel.getSlotDetailsDistrictWise(HomeActivity.districtCode.toInt())

        activityShowSlotsBinding.heading.text = resources.getString(
                R.string.heading_slots,
                HomeActivity.dose,
                HomeActivity.district,
                HomeActivity.age)

        slotDateAdapter = SlotDateAdapter(this, viewModel.getSevenDayDate())
        activityShowSlotsBinding.datesList.adapter = slotDateAdapter
        activityShowSlotsBinding.datesList.layoutManager =
                GridLayoutManager(this, 7, LinearLayoutManager.VERTICAL, false)

        activityShowSlotsBinding.epoxyFilter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityShowSlotsBinding.epoxyFilter.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val filterList = listOf("Free", "Paid", "Covaxin", "Covishield")
                filterList.forEach { filter ->
                    ItemLayoutFilterBindingModel_()
                            .id(filter)
                            .filterName(filter)
                            .onBind { _, view, _ ->
                                val binding = view.dataBinding as ViewholderItemLayoutFilterBinding
                                binding.filter.setOnClickListener {
                                    when (filter) {
                                        "Free" -> {
                                            isFreeClicked = !isFreeClicked
                                            println(isFreeClicked)
                                        }
                                        "Paid" -> {
                                            isPaidClicked = !isPaidClicked
                                            println(isPaidClicked)
                                        }
                                        "Covaxin" -> {
                                            isCovaxinClicked = !isCovaxinClicked
                                            println(isCovaxinClicked)
                                        }
                                        "Covishield" -> {
                                            isCovishieldClicked = !isCovishieldClicked
                                            println(isCovishieldClicked)
                                        }
                                    }

                                    activityShowSlotsBinding.epoxy.requestModelBuild()
                                }
                            }
                            .addTo(controller)
                }
            }
        })

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

                val districtResponse = viewModel.slotDetailsDistrict.value?.data
                val preferredList = mutableListOf<Center>()

                districtResponse?.centers?.forEach { center ->
                    val preferredSessionList = mutableListOf<Session>()
                    val prefFeeType = center.feeType
                    center.sessions?.forEach { session ->

                        var prefDose: Double? = session.availableCapacityDose1
                        if (HomeActivity.dose == "Dose 2") {
                            prefDose = session.availableCapacityDose2
                        }

                        if (prefDose != null) {
                            if (prefDose > 0) {
                                val prefAge = HomeActivity.age.split("[–+]".toRegex()).map { it.trim() }
                                if (session.minAgeLimit == prefAge[0].toInt()) {
                                    session.availableCapacity = prefDose
                                    if (isFreeClicked || isPaidClicked || isCovaxinClicked || isCovishieldClicked) {
                                        if (isFreeClicked && prefFeeType.toString().toLowerCase(Locale.getDefault()) == "free") {
                                            preferredSessionList.add(session)
                                        }

                                        if (isPaidClicked && prefFeeType.toString().toLowerCase(Locale.getDefault()) == "paid") {
                                            preferredSessionList.add(session)
                                        }

                                        if (isCovaxinClicked && session.vaccine.toString().toLowerCase(Locale.getDefault()) == "covaxin") {
                                            preferredSessionList.add(session)
                                        }

                                        if (isCovishieldClicked && session.vaccine.toString().toLowerCase(Locale.getDefault()) == "covishield") {
                                            preferredSessionList.add(session)
                                        }
                                    } else {
                                        preferredSessionList.add(session)
                                    }
                                }
                            }
                        }
                    }
                    if (!preferredSessionList.isNullOrEmpty()) {
                        center.sessions = preferredSessionList
                        preferredList.add(center)
                    }
                }

                if (preferredList.isNullOrEmpty()) {
                    println("No slots available")
                    return
                }

                preferredList.forEach { center ->
                    ItemLayoutCenterHeaderBindingModel_()
                            .id(center.pincode)
                            .centerName(center.name)
                            .pinCode(center.pincode.toString() + " \u2022 " + center.address)
                            .price(center.feeType)
                            .addTo(controller)

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
                            if (session.date!! == currentDateStr) {
                                currentSession = session
                            }
                        }

                        if (currentSession != null) {
                            val colorTint: Int = when {
                                currentSession!!.availableCapacity?.toInt() ?: 0 >= 30 -> {
                                    resources.getColor(R.color.green, applicationContext.theme)
                                }
                                currentSession!!.availableCapacity?.toInt() ?: 0 in 11..29 -> {
                                    resources.getColor(R.color.yellow, applicationContext.theme)
                                }
                                else -> {
                                    resources.getColor(R.color.red, applicationContext.theme)
                                }
                            }
                            subItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                            .id(currentSession.hashCode())
                                            .vaccineName(currentSession!!.vaccine)
                                            .vaccineNo(currentSession!!.availableCapacity?.toInt().toString())
                                            .isEnabled(true)
                                            .backgroundTint(colorTint)
                            )
                        } else {
                            subItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                            .id(Random().nextInt())
                                            .vaccineName("")
                                            .vaccineNo("NA")
                                            .isEnabled(false)
                                            .backgroundTint(resources.getColor(R.color.grey, applicationContext.theme))
                            )
                        }
                    }

                    HorizontalGridSpan7Model_()
                            .id(subItems.hashCode())
                            .models(subItems)
                            .padding(
                                    Carousel.Padding(0, 0, 0, 0, 15)
                            )
                            .addTo(controller)

                    ItemLayoutDividerBindingModel_()
                            .id(Random().nextInt())
                            .addTo(controller)
                }
            }
        })

        viewModel.toast.observe(this, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
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

    companion object {
        var isFreeClicked: Boolean = false
        var isPaidClicked: Boolean = false
        var isCovaxinClicked: Boolean = false
        var isCovishieldClicked: Boolean = false
    }
}