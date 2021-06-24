package com.vaccine.slot.notifier.ui.showSlots

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.databinding.ActivityShowSlotsBinding
import com.vaccine.slot.notifier.other.Constants.BOOK_APPOINTMENT_TAG
import com.vaccine.slot.notifier.other.Constants.FEE_FREE
import com.vaccine.slot.notifier.other.Constants.FEE_PAID
import com.vaccine.slot.notifier.other.HorizontalGridSpan7Model_
import com.vaccine.slot.notifier.other.Status
import com.vaccine.slot.notifier.ui.base.BaseActivity
import com.vaccine.slot.notifier.ui.dialogs.BookAppointmentDialog
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedAge
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDistrictCodeId
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDistrictName
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedDose
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedPinCode
import com.vaccine.slot.notifier.ui.home.HomeActivity.Companion.selectedTab
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class ShowSlots : BaseActivity() {

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
            title = resources.getString(R.string.appBarTitle)
        }

        slotsViewModel = ViewModelProvider(this).get(ShowSlotsViewModel::class.java)

        if (selectedTab == "1") {
            slotsViewModel.getSlotDetailsPinCodeWise(selectedPinCode)
            activityShowSlotsBinding.heading.text = resources.getString(
                R.string.heading_slots,
                selectedDose,
                selectedPinCode,
                selectedAge.toString()
            )
        } else {
            slotsViewModel.getSlotDetailsDistrictWise(selectedDistrictCodeId)
            activityShowSlotsBinding.heading.text = this.resources.getString(
                R.string.heading_slots,
                selectedDose,
                selectedDistrictName,
                selectedAge.toString()
            )
        }

        activityShowSlotsBinding.chipGroupFee.setOnCheckedChangeListener { _, checkedId: Int ->
            try {
                val chip: Chip? =
                    activityShowSlotsBinding.chipGroupFee.findViewById(checkedId) as Chip?
                chip?.let {

                    val predicate: (Center) -> Boolean = { center ->
                        center.feeType.equals(it.text.toString(), ignoreCase = true)
                    }
                    val filter =
                        if (vaccineList.isNullOrEmpty()) originalList?.filterKeys(predicate) else vaccineList?.filterKeys(
                            predicate
                        )
                    feeList = originalList?.filterKeys(predicate)
                    responseList = filter
                } ?: kotlin.run {
                    feeList = HashMap()
                    responseList = if (vaccineList.isNullOrEmpty()) originalList else vaccineList
                }
                activityShowSlotsBinding.epoxy.requestModelBuild()
            } catch (e: Exception) {
                activityShowSlotsBinding.epoxy.requestModelBuild()
                println(e.localizedMessage)
            }
        }

        activityShowSlotsBinding.chipGroupVaccine.setOnCheckedChangeListener { _, checkedId: Int ->
            try {
                val chip: Chip? =
                    activityShowSlotsBinding.chipGroupVaccine.findViewById(checkedId) as Chip?
                chip?.let { c ->

                    val predicate: (Session) -> Boolean = { session ->
                        session.vaccine.equals(c.text.toString(), ignoreCase = true)
                    }
                    val filter = if (feeList.isNullOrEmpty()) originalList?.map {
                        it.key to it.value?.filter(predicate)
                    }?.toMap() else feeList?.map { it.key to it.value?.filter(predicate) }?.toMap()
                    vaccineList =
                        originalList?.map { it.key to it.value?.filter(predicate) }?.toMap()
                    responseList = filter
                } ?: kotlin.run {
                    vaccineList = HashMap()
                    responseList = if (feeList.isNullOrEmpty()) originalList else feeList
                }
                activityShowSlotsBinding.epoxy.requestModelBuild()
            } catch (e: Exception) {
                activityShowSlotsBinding.epoxy.requestModelBuild()
                println(e.localizedMessage)
            }
        }

        activityShowSlotsBinding.dateEpoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.dateEpoxy.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val subItemsDates = mutableListOf<EpoxyModel<*>>()
                slotsViewModel.getSevenDayDate().forEachIndexed { index, item ->
                    val splitDate = item.date.split(" ")
                    subItemsDates.add(
                        ItemLayoutDateBindingModel_()
                            .id(index)
                            .text(splitDate[0] + "\n" + splitDate[1] + " " + splitDate[2])
                    )
                }

                HorizontalGridSpan7Model_()
                    .id(subItemsDates.hashCode())
                    .models(subItemsDates)
                    .padding(
                        Carousel.Padding(0, 0, 0, 0, 8)
                    )
                    .addTo(controller)
            }
        })

        activityShowSlotsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityShowSlotsBinding.epoxy.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

                val response = slotsViewModel.slotDetails.value

                when (response?.status) {
                    Status.LOADING -> {
                        activityShowSlotsBinding.progressBar.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        activityShowSlotsBinding.progressBar.visibility = GONE
                        showSnackbar(
                            response.message ?: resources.getString(R.string.error_message)
                        )
                        noSlotsAvailable()
                    }
                    Status.SUCCESS -> {
                        val nonEmptyListOfResponse =
                            responseList?.filterValues { it?.isNotEmpty() == true }

                        if (nonEmptyListOfResponse.isNullOrEmpty()) noSlotsAvailable() else slotsAvailable()
                        nonEmptyListOfResponse?.map { centerMap ->
                            val center = centerMap.key
                            ItemLayoutCenterHeaderBindingModel_()
                                .id(center.centerId)
                                .centerName(center.name)
                                .price(setPrice(center))
                                .pinCode(center.pincode.toString() + " \u2022 " + center.address)
                                .addTo(controller)

                            val subItems = mutableListOf<EpoxyModel<*>>()
                            val innerSubItems = mutableListOf<EpoxyModel<*>>()
                            for (i in 0..6) {
                                val currentDate = slotsViewModel.getSevenDayDate()[i].date
                                val sdf2 = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
                                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                var date = sdf2.parse(currentDate)

                                val calendar = Calendar.getInstance()
                                calendar.time = date
                                calendar.set(
                                    Calendar.YEAR,
                                    Calendar.getInstance().get(Calendar.YEAR)
                                )

                                date = calendar.time

                                val currentDateStr = sdf.format(date)

                                var currentSession: Session? = null

                                getDistinctDateSessions(centerMap.value)?.forEach { session ->
                                    if (session.date == currentDateStr) {
                                        currentSession = session
                                    }
                                }
                                if (currentSession != null) {
                                    subItems.add(
                                        ItemLayoutSlotsBindingModel_()
                                            .id(currentSession!!.sessionId)
                                            .vaccineName(currentSession?.vaccine)
                                            .vaccineNo(
                                                if (selectedDose == "1") currentSession?.availableCapacityDose1?.toInt()
                                                    .toString()
                                                else currentSession?.availableCapacityDose2?.toInt()
                                                    .toString()
                                            )
                                            .isEnabled(true)
                                            .backgroundTint(getBackgroundColor(currentSession))
                                            .onClick { _ ->
                                                bookAppointmentDialog(
                                                    resources.getString(
                                                        R.string.book_address,
                                                        center.name,
                                                        center.pincode.toString()
                                                    )
                                                )
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
                                                    R.color.darkerGrey,
                                                    applicationContext.theme
                                                )
                                            )
                                            .onClick { _ -> }
                                    )
                                }
                            }

                            HorizontalGridSpan7Model_()
                                .id(subItems.hashCode())
                                .models(subItems)
                                .padding(
                                    Carousel.Padding(0, 0, 0, 0, 8)
                                )
                                .addTo(controller)
                            // show same date sessions
                            getSameDateSessions(centerMap.value)?.forEach { session ->
                                innerSubItems.add(
                                    ItemLayoutSlotsBindingModel_()
                                        .id(session.sessionId)
                                        .vaccineName(session.vaccine)
                                        .vaccineNo(
                                            if (selectedDose == "1") session.availableCapacityDose1?.toInt()
                                                .toString()
                                            else session.availableCapacityDose2?.toInt().toString()
                                        )
                                        .isEnabled(true)
                                        .backgroundTint(getBackgroundColor(session))
                                        .onClick { _ ->
                                            bookAppointmentDialog(
                                                resources.getString(
                                                    R.string.book_address,
                                                    center.name,
                                                    center.pincode.toString()
                                                )
                                            )
                                        }
                                )
                            }

                            if (innerSubItems.size > 0) {
                                HorizontalGridSpan7Model_()
                                    .id(innerSubItems.hashCode())
                                    .models(innerSubItems)
                                    .padding(
                                        Carousel.Padding(0, 0, 0, 0, 8)
                                    )
                                    .addTo(controller)
                            }

                            ItemLayoutDividerBindingModel_()
                                .id(Random().nextInt())
                                .addTo(controller)
                        }
                        activityShowSlotsBinding.progressBar.visibility = GONE
                    }
                }
            }
        })

        slotsViewModel.slotDetails.observe(this,
            { it ->
                val allValidSessions = it?.data?.centers?.sortedBy { it.name }?.map { center ->
                    center to center.sessions?.filter { session ->
                        isValidSession(
                            session,
                            selectedAge.toString(),
                            selectedDose
                        )
                    }
                }?.toMap()

                originalList = allValidSessions
                responseList = originalList

                slotsViewModel.setChipFilterList(originalList) // fetch unique fee type and vaccine type

                activityShowSlotsBinding.epoxy.requestModelBuild()
            })

        slotsViewModel.chipFilterList.observe(this,
            {
                addChips(it)
            })
    }

    private fun setPrice(center: Center): String {
        val vaccineFee = center.vaccineFee
        return if (center.feeType.equals(
                FEE_PAID,
                ignoreCase = true
            )
        ) try {
            if (vaccineFee?.size == 1) ("\u20B9" + vaccineFee[0].fee) else (vaccineFee?.let {
                "\u20B9" + it.minByOrNull { f -> f.fee!!.toInt() }?.fee + "+"
            } ?: FEE_PAID)
        } catch (e: Exception) {
            FEE_PAID
        } else FEE_FREE
    }

    private fun isValidSession(session: Session, s: String?, selectedDose: String): Boolean {
        if (session.minAgeLimit == s?.toInt() && (if (selectedDose == "1") session.availableCapacityDose1!! > 0 else session.availableCapacityDose2!! > 0)) return true
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
        activityShowSlotsBinding.noSlotsImage.visibility = VISIBLE
        activityShowSlotsBinding.noSlotsMessage.visibility = VISIBLE
        activityShowSlotsBinding.noSlotsMessage.text = resources.getString(R.string.no_slots)
    }

    private fun slotsAvailable() {
        activityShowSlotsBinding.noSlotsImage.visibility = GONE
        activityShowSlotsBinding.noSlotsMessage.visibility = GONE
    }

    private fun getDistinctDateSessions(sessionList: List<Session>?) =
        sessionList?.distinctBy { it.date }

    private fun getSameDateSessions(sessionList: List<Session>?) =
        sessionList?.duplicateBy { it.date }

    private fun <T, K> Iterable<T>.duplicateBy(selector: (T) -> K): List<T> {
        val set = HashSet<K>()
        val temp = ArrayList<T>()
        val list = ArrayList<T>()
        for (e in this) {
            val key = selector(e)
            if (set.add(key))
                temp.add(e)
            else
                list.add(e)
        }
        return list
    }

    private fun getBackgroundColor(session: Session?) =
        if (selectedDose == "1")
            when {
                session!!.availableCapacityDose1?.toInt() ?: 0 >= 30 -> {
                    resources.getColor(R.color.green, applicationContext.theme)
                }
                session.availableCapacityDose1?.toInt() ?: 0 in 11..29 -> {
                    resources.getColor(R.color.yellow, applicationContext.theme)
                }
                else -> {
                    resources.getColor(R.color.red, applicationContext.theme)
                }
            }
        else
            when {
                session!!.availableCapacityDose2?.toInt() ?: 0 >= 30 -> {
                    resources.getColor(R.color.green, applicationContext.theme)
                }
                session.availableCapacityDose2?.toInt() ?: 0 in 11..29 -> {
                    resources.getColor(R.color.yellow, applicationContext.theme)
                }
                else -> {
                    resources.getColor(R.color.red, applicationContext.theme)
                }
            }

    private fun bookAppointmentDialog(title: String) {
        BookAppointmentDialog.newInstance(title).show(supportFragmentManager, BOOK_APPOINTMENT_TAG)
    }

    private fun showSnackbar(message: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}