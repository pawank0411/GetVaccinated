package com.vaccine.slot.notifier.ui.showSlots

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import com.vaccine.slot.notifier.databinding.BookAppointmentDialogLayoutBinding
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedAge
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDistrictCodeId
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDistrictName
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDose
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedPinCode
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedTab
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
        if (selectedTab == "0") {
            viewModel.getSlotDetailsDistrictWise(selectedDistrictCodeId)
            activityShowSlotsBinding.heading.text = this.resources.getString(
                R.string.heading_slots,
                selectedDose,
                selectedDistrictName,
                selectedAge
            )
        } else {
            viewModel.getSlotDetailsPinCodeWise(selectedPinCode.toInt())
            activityShowSlotsBinding.heading.text = resources.getString(
                R.string.heading_slots,
                selectedDose,
                selectedPinCode,
                selectedAge
            )
        }

        slotDateAdapter = SlotDateAdapter(this, viewModel.getSevenDayDate())
        activityShowSlotsBinding.datesList.adapter = slotDateAdapter
        activityShowSlotsBinding.datesList.layoutManager =
                GridLayoutManager(this, 7, LinearLayoutManager.VERTICAL, false)

        val bottomSheetDialog = BottomSheetDialog(this)
        val bookAppointmentDialogLayoutBinding = BookAppointmentDialogLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bookAppointmentDialogLayoutBinding.root)

        activityShowSlotsBinding.epoxyFilter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityShowSlotsBinding.epoxyFilter.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val filterList = listOf("Free", "Paid", "Covaxin", "Covishield")
                filterList.forEach { filter ->
                    ItemLayoutFilterBindingModel_()
                            .id(filter)
                            .filterName(filter)
                            .onClick { _ ->
                                when (filter) {
                                    "Free" -> {
                                        isFreeClicked = !isFreeClicked
                                    }
                                    "Paid" -> {
                                        isPaidClicked = !isPaidClicked
                                    }
                                    "Covaxin" -> {
                                        isCovaxinClicked = !isCovaxinClicked
                                    }
                                    "Covishield" -> {
                                        isCovishieldClicked = !isCovishieldClicked
                                    }
                                }
                                activityShowSlotsBinding.epoxy.requestModelBuild()
                            }
                            .addTo(controller)
                }
            }
        })

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

                val apiResponse = viewModel.slotDetails.value?.data
                var preferredList = listOf<Center>()

                if (isFreeClicked) {
                    preferredList = if (preferredList.isNullOrEmpty()) filterList(
                        "free",
                        "free",
                        apiResponse?.centers
                    )
                    else filterList("free", "free", preferredList)
                }

                if (isPaidClicked) {
                    preferredList = if (preferredList.isNullOrEmpty()) filterList(
                        "paid",
                        "free",
                        apiResponse?.centers
                    )
                    else filterList("paid", "free", preferredList)
                }

                if (isCovaxinClicked) {
                    preferredList = if (preferredList.isNullOrEmpty()) filterList(
                        "covaxin",
                        "vaccine",
                        apiResponse?.centers
                    )
                    else filterList("covaxin", "vaccine", preferredList)
                }

                if (isCovishieldClicked) {
                    preferredList = if (preferredList.isNullOrEmpty()) filterList(
                        "covishield",
                        "vaccine",
                        apiResponse?.centers
                    )
                    else filterList("covishield", "vaccine", preferredList)
                }

//                apiResponse?.centers?.forEach { center ->
//                    val preferredSessionList = mutableListOf<Session>()
//                    val prefFeeType = center.feeType
//                    center.sessions?.forEach { session ->
//
//                        var prefDose: Double? = session.availableCapacityDose1
//                        if (selectedDose == "Dose 2") {
//                            prefDose = session.availableCapacityDose2
//                        }
//
//                        if (prefDose != null) {
//                            if (prefDose > 0) {
//                                val prefAge = selectedAge.split("[–+]".toRegex()).map { it.trim() }
//                                if (session.minAgeLimit == prefAge[0].toInt()) {
//                                    session.availableCapacity = prefDose
//                                    if (isFreeClicked || isPaidClicked || isCovaxinClicked || isCovishieldClicked) {
//                                        if (isFreeClicked && prefFeeType.toString().toLowerCase(Locale.getDefault()) == "free") {
//                                            preferredSessionList.add(session)
//                                        }
//
//                                        if (isPaidClicked && prefFeeType.toString().toLowerCase(Locale.getDefault()) == "paid") {
//                                            preferredSessionList.add(session)
//                                        }
//
//                                        if (isCovaxinClicked && session.vaccine.toString().toLowerCase(Locale.getDefault()) == "covaxin") {
//                                            preferredSessionList.add(session)
//                                        }
//
//                                        if (isCovishieldClicked && session.vaccine.toString().toLowerCase(Locale.getDefault()) == "covishield") {
//                                            preferredSessionList.add(session)
//                                        }
//                                    } else {
//                                        preferredSessionList.add(session)
//                                    }
//                                }
//                            }
//                        }
//                    } ?: noSlotsAvailable()
//
//                    if (!preferredSessionList.isNullOrEmpty()) {
//                        activityShowSlotsBinding.noSlotsMessage.visibility = GONE
//                        center.sessions = preferredSessionList
//                        preferredList.add(center)
//                    }
//
//                    if (preferredList.isNullOrEmpty())
//                        noSlotsAvailable()
//
//                } ?: noSlotsAvailable()

                println(preferredList)
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
                        } ?: noSlotsAvailable()

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
                                    .vaccineNo(
                                        currentSession!!.availableCapacity?.toInt().toString()
                                    )
                                    .isEnabled(true)
                                    .backgroundTint(colorTint)
                                    .onClick { _ ->
                                        bookAppointmentDialogLayoutBinding.centerName.text =
                                            resources.getString(
                                                R.string.book_address,
                                                center.name,
                                                center.pincode.toString()
                                            )
                                        bookAppointmentDialogLayoutBinding.bookAppointment.setOnClickListener {
                                            openCoWinWebsite()
                                        }
                                        bookAppointmentDialogLayoutBinding.close.setOnClickListener {
                                            bottomSheetDialog.dismiss()
                                        }
                                        bottomSheetDialog.show()
                                    }
                            )
                        } else {
                            subItems.add(
                                ItemLayoutSlotsBindingModel_()
                                    .id(Random().nextInt())
                                    .vaccineName("")
                                    .vaccineNo("NA")
                                    .isEnabled(false)
                                    .backgroundTint(
                                        resources.getColor(
                                            R.color.grey,
                                            applicationContext.theme
                                        )
                                    )
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

        viewModel.slotDetails.observe(this, {
            activityShowSlotsBinding.epoxy.requestModelBuild()
        })
    }

    private fun filterList(
        filterString: String,
        filterType: String,
        originalList: List<Center>?
    ): List<Center> {
        val filteredList = mutableListOf<Center>()
        if (filterType == "free") {
            originalList?.filterTo(filteredList) { center ->
                center.feeType.equals(filterString, ignoreCase = true)
            }
        } else {
            originalList?.filterTo(filteredList) { center ->
                center.sessions?.forEach { session ->
                    session.vaccine.equals(filterString, ignoreCase = true)
                }
                true
            }
        }
        return filteredList
    }

    private fun noSlotsAvailable() {
        activityShowSlotsBinding.noSlotsMessage.visibility = VISIBLE
        activityShowSlotsBinding.noSlotsMessage.text = resources.getString(R.string.no_slots)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun openCoWinWebsite() {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(this, Uri.parse("https://selfregistration.cowin.gov.in/"))
    }

    companion object {
        var isFreeClicked: Boolean = false
        var isPaidClicked: Boolean = false
        var isCovaxinClicked: Boolean = false
        var isCovishieldClicked: Boolean = false
    }
}