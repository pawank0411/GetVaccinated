package com.vaccine.slot.notifier.ui.showSlots

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
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
import com.google.android.material.snackbar.Snackbar
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import com.vaccine.slot.notifier.databinding.LayoutBookAppointmentDialogBinding
import com.vaccine.slot.notifier.databinding.LayoutProgressDialogBinding
import com.vaccine.slot.notifier.other.HorizontalGridSpan7Model_
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
import kotlin.collections.HashMap

@AndroidEntryPoint
class ShowSlots : AppCompatActivity() {

    private lateinit var activityShowSlotsBinding: ActivityShowSlotsBinding
    private lateinit var slotsViewModel: ShowSlotsViewModel
    private var feeList: Map<Center, List<Session>?>? = HashMap()
    private var vaccineList: Map<Center, List<Session>?>? = HashMap()
    private var responseList: Map<Center, List<Session>?>? = HashMap()
    private var originalList: Map<Center, List<Session>?>? = HashMap()

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
                val filter = if (vaccineList.isNullOrEmpty()) originalList?.filterKeys(predicate) else vaccineList?.filterKeys(predicate)
                feeList = originalList?.filterKeys(predicate)
                responseList = filter
            } ?: kotlin.run {
                feeList = HashMap()
                responseList = if (vaccineList.isNullOrEmpty()) originalList else vaccineList
            }
            activityShowSlotsBinding.epoxy.requestModelBuild()
        }

        activityShowSlotsBinding.chipGroupVaccine.setOnCheckedChangeListener { _, checkedId: Int ->
            val chip: Chip? = activityShowSlotsBinding.chipGroupVaccine.findViewById(checkedId) as Chip?
            chip?.let { c ->

                val predicate: (Session) -> Boolean = { session ->
                    session.vaccine.equals(c.text.toString(), ignoreCase = true)
                }
                val filter = if (feeList.isNullOrEmpty()) originalList?.map { it.key to it.value?.filter(predicate) }?.toMap() else feeList?.map { it.key to it.value?.filter(predicate) }?.toMap()
                vaccineList = originalList?.map { it.key to it.value?.filter(predicate) }?.toMap()
                responseList = filter
            } ?: kotlin.run {
                vaccineList = HashMap()
                responseList = if (feeList.isNullOrEmpty()) originalList else feeList
            }
            activityShowSlotsBinding.epoxy.requestModelBuild()
        }

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val subItemsDates = mutableListOf<EpoxyModel<*>>()
                slotsViewModel.getSevenDayDate().forEachIndexed { index, item ->
                    val splitDate = item.date.split(" ")
                    subItemsDates.add(ItemLayoutDateBindingModel_()
                            .id(index)
                            .text(splitDate[0] + "\n" + splitDate[1] + " " + splitDate[2]))
                }

                HorizontalGridSpan7Model_()
                        .id(subItemsDates.hashCode())
                        .models(subItemsDates)
                        .padding(
                                Carousel.Padding(0, 0, 0, 0, 15)
                        )
                        .addTo(controller)

                val response = slotsViewModel.slotDetails.value
                when (response?.status) {
                    Status.LOADING -> {
                        bottomProgressDialog.show()
                    }
                    Status.ERROR -> {
                        bottomProgressDialog.dismiss()
                        showSnackbar(response.message ?: "Something went wrong. Please try again")
                        noSlotsAvailable()
                    }
                    Status.SUCCESS -> {
                        if (responseList.isNullOrEmpty()) noSlotsAvailable() else activityShowSlotsBinding.noSlotsMessage.visibility = GONE
                        val nonEmptyListOfResponse = responseList?.filterValues { it?.isNotEmpty() == true }
                        nonEmptyListOfResponse?.map { centerMap ->
                            val center = centerMap.key
                            ItemLayoutCenterHeaderBindingModel_()
                                    .id(center.centerId)
                                    .centerName(center.name)
                                    .price(center.feeType)
                                    .pinCode(center.pincode.toString() + " \u2022 " + center.address)
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

                                centerMap.value?.forEach { session ->
                                    if (session.date!! == currentDateStr) {
                                        currentSession = session
                                    }
                                }

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


                        }
                        bottomProgressDialog.dismiss()
                    }
                }
            }
        })

        slotsViewModel.slotDetails.observe(this, { it ->
            slotsViewModel.setChipFilterList(it?.data?.centers) // fetch unique fee type and vaccine type

            val prefAge = selectedAge.split("[–+]".toRegex()).map { it.trim() }

            val allValidSessions = it?.data?.centers?.sortedBy { it.name }?.map { center ->
                center to center.sessions?.filter { session -> isValidSession(session, prefAge[0], selectedDose) }
            }?.toMap()

            originalList = allValidSessions
            responseList = originalList
            activityShowSlotsBinding.epoxy.requestModelBuild()
        })

        slotsViewModel.chipFilterList.observe(this, {
            addChips(it)
        })
    }

    private fun isValidSession(session: Session, s: String, selectedDose: String): Boolean {
        if (session.minAgeLimit!! == s.toInt() && (if (selectedDose == "1") session.availableCapacityDose1!! > 0 else session.availableCapacityDose2!! > 0)) return true
        return false
    }

    private fun addChips(chipSet: Set<String>) {
        activityShowSlotsBinding.chipGroupFee.removeAllViews()
        activityShowSlotsBinding.chipGroupVaccine.removeAllViews()
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

    private fun openCoWinWebsite() {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(this, Uri.parse("https://selfregistration.cowin.gov.in/"))
    }

    private fun showSnackbar(message: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}