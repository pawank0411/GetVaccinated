package com.vaccine.slot.notifier.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.databinding.*
import com.vaccine.slot.notifier.ui.notification.NotificationMessage
import com.vaccine.slot.notifier.ui.showSlots.ShowSlots
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetLayoutBinding: LayoutStatedistrcitListBinding
    private val titles = listOf("Search By District", "Search By PIN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(false)
            title = ""
        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetLayoutBinding = LayoutStatedistrcitListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)
        bottomSheetLayoutBinding.dataList.layoutManager = LinearLayoutManager(this)

        homeViewModel.getStateList() // pre-fetch state list
        populateStateList()

        if (selectedTab.isEmpty()) buildTabContent("District", false) // default selected tab
        activityHomeBinding.epoxyTabs.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityHomeBinding.epoxyTabs.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                titles.forEach { title ->
                    val background = when (homeViewModel.tabSelection.value) {
                        0 -> {
                            if (title.contains("District")) {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.rounded_corners
                                )
                            } else {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.transparent_background
                                )
                            }
                        }
                        1 -> {
                            if (title.contains("PIN")) {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.rounded_corners
                                )
                            } else {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.transparent_background
                                )
                            }
                        }
                        else -> ContextCompat.getDrawable(
                                this@HomeActivity,
                                R.drawable.transparent_background
                        )
                    }
                    ItemLayoutTabBindingModel_()
                            .id(Random().nextInt())
                            .text(title)
                            .background(background)
                            .onClick { _ ->
                                homeViewModel.setTabSelected(if (title.contains("District")) 0 else 1)
                                homeViewModel.getContentList(title)
                                buildTabContent(title, true)
                            }
                            .addTo(controller)
                }
            }
        })

        activityHomeBinding.epoxyCarousel.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                // TODO Carousel
            }

        })

        activityHomeBinding.checkAvailability.setOnClickListener {
            when (activityHomeBinding.ageGroup.checkedRadioButtonId) {
                R.id.age1 ->
                    selectedAge = activityHomeBinding.age1.text.toString()
                R.id.age2 ->
                    selectedAge = activityHomeBinding.age2.text.toString()
            }

            when (activityHomeBinding.doseGroup.checkedRadioButtonId) {
                R.id.dose1 ->
                    selectedDose = activityHomeBinding.dose1.text.toString().split(" ")[1]
                R.id.dose2 ->
                    selectedDose = activityHomeBinding.dose2.text.toString().split(" ")[1]
            }

            val bottomSheetDialog = BottomSheetDialog(this)
            val messageDialogLayoutBinding = LayoutMessageDialogBinding.inflate(layoutInflater)
            bottomSheetDialog.setContentView(messageDialogLayoutBinding.root)

            if (homeViewModel.tabSelection.value == 0 && (selectedStateName.isEmpty() || selectedDistrictName.isEmpty())) {
                messageDialogLayoutBinding.message.text = resources.getString(R.string.select_error)
                messageDialogLayoutBinding.close.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            } else if (homeViewModel.tabSelection.value == 1 && (selectedPinCode.isEmpty() || selectedPinCode.length < 6)) {
                messageDialogLayoutBinding.message.text =
                        resources.getString(R.string.pincode_error)
                messageDialogLayoutBinding.close.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            } else {
                startActivity(Intent(this, ShowSlots::class.java))
            }
        }

        homeViewModel.userText.observe(this, {
            selectedPinCode = it
        })

        homeViewModel.stateList.observe(this, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        homeViewModel.districtList.observe(this, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        homeViewModel.tabSelection.observe(this, {
            selectedTab = it.toString()
            if (selectedTab == "0") buildTabContent("District", true) else buildTabContent("PIN", true)
        })
        activityHomeBinding.footerTextTitle.movementMethod = LinkMovementMethod.getInstance()
        activityHomeBinding.footerTextTitle.setLinkTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.blueF
                )
        )
    }

    private fun buildTabContent(title: String, requestRebuild: Boolean) {
        when {
            title.contains("District") -> {
                activityHomeBinding.epoxyTabContent.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                activityHomeBinding.epoxyTabContent.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        val hintTextList = homeViewModel.contentTabDistrict.value
                        for (i in 0..1) {
                            ItemLayoutDistrictBindingModel_()
                                    .id(i)
                                    .hintText(hintTextList?.get(i))
                                    .text(if (i == 0) selectedStateName else selectedDistrictName)
                                    .onClick { _ ->
                                        when (i) {
                                            0 -> {
                                                bottomSheetLayoutBinding.bottomSheetTitle.text = resources.getString(R.string.select_your_state)
                                                populateStateList()
                                                bottomSheetDialog.show()
                                            }
                                            1 -> {
                                                bottomSheetLayoutBinding.bottomSheetTitle.text = resources.getString(R.string.select_your_district)
                                                populateStateDistrictList()
                                                bottomSheetDialog.show()
                                            }
                                        }
                                    }
                                    .addTo(controller)
                        }
                    }

                })
            }
            title.contains("PIN") -> {
                activityHomeBinding.epoxyTabContent.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                activityHomeBinding.epoxyTabContent.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                    override fun buildModels(controller: EpoxyController) {
                        val hintTextList = homeViewModel.contentTabPincode.value
                        ItemLayoutPincodeBindingModel_()
                                .id(Random().nextInt())
                                .hintText(hintTextList?.get(0))
                                .viewModel(homeViewModel)
                                .addTo(controller)
                    }
                })
            }
        }

        if (requestRebuild)
            activityHomeBinding.epoxyTabs.requestModelBuild()

    }

    private fun populateStateList() {
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = homeViewModel.stateList.value
                dataList?.forEach { state ->
                    ItemLayoutBottomSheetBindingModel_()
                            .id(state.stateId)
                            .name(state.stateName)
                            .onClick { _ ->
                                selectedStateName = state.stateName
                                homeViewModel.getDistrictList(state.stateId) // pre-fetch all districts of state
                                selectedDistrictName = ""
                                bottomSheetDialog.dismiss()
                                activityHomeBinding.epoxyTabContent.requestModelBuild()
                            }
                            .addTo(controller)
                } ?: bottomSheetDialog.dismiss()
            }
        })
    }

    private fun populateStateDistrictList() {
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = homeViewModel.districtList.value
                dataList?.forEach { district ->
                    ItemLayoutBottomSheetBindingModel_()
                            .id(district.districtId)
                            .name(district.districtName)
                            .onClick { _ ->
                                selectedDistrictName = district.districtName
                                selectedDistrictCodeId = district.districtId
                                bottomSheetDialog.dismiss()
                                activityHomeBinding.epoxyTabContent.requestModelBuild()
                            }
                            .addTo(controller)
                } ?: bottomSheetDialog.dismiss()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "SAMPLE TEXT"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
            return true
        } else if (id == R.id.action_notification) {
            startActivity(Intent(this@HomeActivity, NotificationMessage::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var selectedAge: String = String()
        var selectedDose: String = String()
        var selectedTab: String = String()
        var selectedPinCode: String = String()
        var selectedStateName: String = String()
        var selectedDistrictName: String = String()
        var selectedDistrictCodeId: Int = 0
    }
}