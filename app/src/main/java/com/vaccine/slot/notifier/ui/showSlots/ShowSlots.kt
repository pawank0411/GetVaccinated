package com.vaccine.slot.notifier.ui.showSlots

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import com.vaccine.slot.notifier.databinding.LayoutBookAppointmentDialogBinding
import com.vaccine.slot.notifier.databinding.LayoutProgressDialogBinding
import com.vaccine.slot.notifier.other.Status
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
    private lateinit var slotsViewModel: ShowSlotsViewModel
    private var feeList: List<Center>? = listOf()
    private var vaccineList: List<Center>? = listOf()
    private var responseList: List<Center>? = listOf()
    private var originalList: List<Center>? = listOf()

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

        slotsViewModel = ViewModelProvider(this).get(ShowSlotsViewModel::class.java)

        val bottomBookDialog = BottomSheetDialog(this)
        val bookAppointmentDialogLayoutBinding = LayoutBookAppointmentDialogBinding.inflate(layoutInflater)
        bottomBookDialog.setContentView(bookAppointmentDialogLayoutBinding.root)

        val bottomProgressDialog = BottomSheetDialog(this)
        val layoutProgressDialogBinding = LayoutProgressDialogBinding.inflate(layoutInflater)
        bottomProgressDialog.setContentView(layoutProgressDialogBinding.root)

        activityShowSlotsBinding.datesList.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.datesList.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val subItems = mutableListOf<EpoxyModel<*>>()
                slotsViewModel.getSevenDayDate().forEachIndexed { index, item ->
                    val splitDate = item.date.split(" ")
                    subItems.add(ItemLayoutDateBindingModel_()
                            .id(index)
                            .text(splitDate[0] + "\n" + splitDate[1] + " " + splitDate[2]))
                }

                HorizontalGridSpan7Model_()
                        .id(subItems.hashCode())
                        .models(subItems)
                        .padding(
                                Carousel.Padding(0, 0, 0, 0, 15)
                        )
                        .addTo(controller)
            }
        })
        println(selectedTab)
        if (selectedTab == "1") {
            slotsViewModel.getSlotDetailsPinCodeWise(selectedPinCode)
            activityShowSlotsBinding.heading.text = resources.getString(
                    R.string.heading_slots,
                    selectedDose,
                    selectedPinCode,
                    selectedAge
            )
        } else {
            slotsViewModel.getSlotDetailsDistrictWise(selectedDistrictCodeId)
            activityShowSlotsBinding.heading.text = this.resources.getString(
                    R.string.heading_slots,
                    selectedDose,
                    selectedDistrictName,
                    selectedAge
            )
        }

        activityShowSlotsBinding.chipGroupFee.setOnCheckedChangeListener { _, checkedId: Int ->
            val chip: Chip? = activityShowSlotsBinding.chipGroupFee.findViewById(checkedId) as Chip?
            chip?.let {
                val predicate: (Center) -> Boolean = { center ->
                    center.feeType.equals(it.text.toString(), ignoreCase = true)
                }

                val filter = if (vaccineList.isNullOrEmpty()) originalList?.filter(predicate) else vaccineList?.filter(predicate)
                responseList = filter
                feeList = originalList?.filter(predicate)
            } ?: kotlin.run {
                feeList = listOf()
                responseList = if (vaccineList.isNullOrEmpty()) originalList else vaccineList
            }
            activityShowSlotsBinding.epoxy.requestModelBuild()
        }

        activityShowSlotsBinding.chipGroupVaccine.setOnCheckedChangeListener { _, checkedId: Int ->
            val chip: Chip? = activityShowSlotsBinding.chipGroupVaccine.findViewById(checkedId) as Chip?
            chip?.let {
                val predicate: (Session) -> Boolean = { session ->
                    session.vaccine.equals(it.text.toString(), ignoreCase = true)
                }

                val filter = if (feeList.isNullOrEmpty()) originalList?.filter { center ->
                    center.sessions?.any(predicate) == true
                } else feeList?.filter { center ->
                    center.sessions?.any(predicate) == true
                }
                responseList = filter
                vaccineList = originalList?.filter { center ->
                    center.sessions?.any(predicate) == true
                }
            } ?: kotlin.run {
                vaccineList = listOf()
                responseList = if (feeList.isNullOrEmpty()) originalList else feeList
            }
            activityShowSlotsBinding.epoxy.requestModelBuild()
        }

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val response = slotsViewModel.slotDetails.value
                when (response?.status) {
                    Status.LOADING -> {
                        bottomProgressDialog.show()
                    }
                    Status.ERROR -> {
                        // TODO snack the error message
                        bottomProgressDialog.dismiss()
                    }
                    Status.SUCCESS -> {
                        val sortedList = responseList?.sortedBy { center -> center.name }
                        sortedList?.forEachIndexed { index, center ->
                            ItemLayoutCenterHeaderBindingModel_()
                                    .id(index)
                                    .centerName(center.name)
                                    .pinCode(center.pincode.toString() + " \u2022 " + center.address)
                                    .price(center.feeType)
                                    .addTo(controller)

                            val subItems = mutableListOf<EpoxyModel<*>>()
                            for (i in 0..6) {
                                val currentDate = slotsViewModel.getSevenDayDate()[i].date
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
                                    val colorTint: Int
                                    if (selectedDose == "1")
                                        colorTint = when {
                                            currentSession!!.availableCapacityDose1?.toInt() ?: 0 >= 30 -> {
                                                resources.getColor(R.color.green, applicationContext.theme)
                                            }
                                            currentSession!!.availableCapacityDose1?.toInt() ?: 0 in 11..29 -> {
                                                resources.getColor(R.color.yellow, applicationContext.theme)
                                            }
                                            else -> {
                                                resources.getColor(R.color.red, applicationContext.theme)
                                            }
                                        }
                                    else
                                        colorTint = when {
                                            currentSession!!.availableCapacityDose2?.toInt() ?: 0 >= 30 -> {
                                                resources.getColor(R.color.green, applicationContext.theme)
                                            }
                                            currentSession!!.availableCapacityDose2?.toInt() ?: 0 in 11..29 -> {
                                                resources.getColor(R.color.yellow, applicationContext.theme)
                                            }
                                            else -> {
                                                resources.getColor(R.color.red, applicationContext.theme)
                                            }
                                        }

                                    subItems.add(
                                            ItemLayoutSlotsBindingModel_()
                                                    .id(i)
                                                    .vaccineName(currentSession!!.vaccine)
                                                    .vaccineNo(
                                                            if (selectedDose == "1") currentSession!!.availableCapacityDose1?.toInt().toString()
                                                            else currentSession!!.availableCapacityDose2?.toInt().toString()
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
                                                            bottomBookDialog.dismiss()
                                                        }
                                                        bottomBookDialog.show()
                                                    }
                                    )
                                } else {
                                    subItems.add(
                                            ItemLayoutSlotsBindingModel_()
                                                    .id(i)
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

                        } ?: noSlotsAvailable()
                        bottomProgressDialog.dismiss()
                    }
                }
            }
        })

        slotsViewModel.slotDetails.observe(this, {
            val chipSet = mutableSetOf<String>()
            val prefAge = selectedAge.split("[–+]".toRegex()).map { it.trim() }
            val predicate: (Session) -> Boolean = { session ->
                session.minAgeLimit == prefAge[0].toInt() && if (selectedDose == "1") session.availableCapacityDose1!! > 0
                else session.availableCapacityDose2!! > 0
            }
            originalList = it.data?.centers?.filter { center ->
                center.sessions?.any { session ->
                    session.minAgeLimit == prefAge[0].toInt() && if (selectedDose == "1") session.availableCapacityDose1!! > 0
                    else session.availableCapacityDose2!! > 0
                } == true
            }

            originalList?.forEach { center ->
                center.feeType?.let { it1 -> chipSet.add(it1.capitalizeWords()) }
                center.sessions?.forEach { session ->
                    session.vaccine?.let { it1 -> chipSet.add(it1.capitalizeWords()) }
                }
            }
            addChips(chipSet)
            responseList = originalList
            activityShowSlotsBinding.epoxy.requestModelBuild()
        })
    }

    private fun addChips(chipSet: MutableSet<String>) {
        // TODO configuration changes
        chipSet.forEach { filter ->
            val chip = Chip(this)
            chip.text = filter
            chip.chipBackgroundColor = slotsViewModel.backgroundColorStateList()
            chip.setTextColor(slotsViewModel.textColorStateList())
            chip.setRippleColorResource(R.color.black)
            chip.setCheckedIconTintResource(R.color.white)
            chip.isCheckable = true
            if (filter.contains("Free") || filter.contains("Paid")) {
                activityShowSlotsBinding.chipGroupFee.addView(chip)
                activityShowSlotsBinding.chipGroupFee.visibility = VISIBLE
            } else {
                activityShowSlotsBinding.chipGroupVaccine.addView(chip)
                activityShowSlotsBinding.chipGroupVaccine.visibility = VISIBLE
            }
        }
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

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
        it.capitalize(Locale.ROOT)
    }

    private fun openCoWinWebsite() {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(this, Uri.parse("https://selfregistration.cowin.gov.in/"))
    }
}