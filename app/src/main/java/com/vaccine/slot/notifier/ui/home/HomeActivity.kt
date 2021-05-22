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
import com.vaccine.slot.notifier.ItemLayoutBottomSheetBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityHomeBinding
import com.vaccine.slot.notifier.databinding.BottomSheetLayoutBinding
import com.vaccine.slot.notifier.databinding.MessageDialogLayoutBinding
import com.vaccine.slot.notifier.databinding.ViewholderItemLayoutBottomSheetBinding
import com.vaccine.slot.notifier.ui.showSlots.ShowSlots
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog

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

        activityHomeBinding.footerTextTitle.movementMethod = LinkMovementMethod.getInstance()
        activityHomeBinding.footerTextTitle.setLinkTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.blueF
                )
        )

        viewModel = ViewModelProvider(this).get(HomeViewModel(this)::class.java)
        viewModel.getStateList()

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)

        populateStateList()

        activityHomeBinding.epoxyCarousel.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

            }

        })

        activityHomeBinding.stateNameEditText.setOnClickListener {
            // populates state list
            bottomSheetLayoutBinding.bottomSheetTitle.text =
                    resources.getString(R.string.select_your_state)
            populateStateList()
            bottomSheetDialog.show()
        }

        activityHomeBinding.districtEditText.setOnClickListener {
            bottomSheetLayoutBinding.bottomSheetTitle.text =
                    resources.getString(R.string.select_your_district)
            populateStateDistrictList() // populates stateDistrict list
        }

        viewModel.stateList.observe(this, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        viewModel.districtList.observe(this, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        activityHomeBinding.checkAvailability.setOnClickListener {
            pincode = activityHomeBinding.pincodeEditText.text.toString()
            state = activityHomeBinding.stateNameEditText.text.toString()
            district = activityHomeBinding.districtEditText.text.toString()

            when (activityHomeBinding.ageGroup.checkedRadioButtonId) {
                R.id.age1 ->
                    age = activityHomeBinding.age1.text.toString()
                R.id.age2 ->
                    age = activityHomeBinding.age2.text.toString()
            }

            when (activityHomeBinding.doseGroup.checkedRadioButtonId) {
                R.id.dose1 ->
                    dose = activityHomeBinding.dose1.text.toString()
                R.id.dose2 ->
                    dose = activityHomeBinding.dose2.text.toString()
            }

            if ((state.isEmpty() || district.isEmpty() || age.isEmpty() || dose.isEmpty()) && pincode.isEmpty()) {
                val bottomSheetDialog = BottomSheetDialog(this)
                val messageDialogLayoutBinding = MessageDialogLayoutBinding.inflate(layoutInflater)
                bottomSheetDialog.setContentView(messageDialogLayoutBinding.root)

                messageDialogLayoutBinding.message.text = resources.getString(R.string.select_error)
                messageDialogLayoutBinding.close.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            } else
                startActivity(Intent(this, ShowSlots::class.java))
        }
    }

    private fun populateStateList() {
        bottomSheetLayoutBinding.dataList.layoutManager = LinearLayoutManager(this)
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = viewModel.stateList.value
                if (dataList != null) {
                    for (i in dataList) {
                        ItemLayoutBottomSheetBindingModel_()
                                .id(i)
                                .name(i)
                                .onBind { _, view, _ ->
                                    val binding =
                                            view.dataBinding as ViewholderItemLayoutBottomSheetBinding
                                    binding.parentLayout.setOnClickListener {
                                        activityHomeBinding.stateNameEditText.setText(i)
                                        viewModel.getPreferredStateDistrict(i) // FETCHES DISTRICT OF PARTICULAR STATE
                                        if (activityHomeBinding.districtEditText.text?.isNotBlank() == true)
                                            activityHomeBinding.districtEditText.text = null
                                        bottomSheetDialog.dismiss()
                                    }
                                }
                                .addTo(controller)
                    }
                }
            }
        })
    }

    private fun populateStateDistrictList() {
        bottomSheetLayoutBinding.dataList.layoutManager = LinearLayoutManager(this)
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = viewModel.districtList.value
                if (dataList != null) {
                    for (i in dataList) {
                        ItemLayoutBottomSheetBindingModel_()
                                .id(i.code)
                                .name(i.name)
                                .onBind { _, view, _ ->
                                    val binding =
                                            view.dataBinding as ViewholderItemLayoutBottomSheetBinding
                                    binding.parentLayout.setOnClickListener {
                                        activityHomeBinding.districtEditText.setText(i.name)
                                        districtCode = i.code.toString()
                                        bottomSheetDialog.dismiss()
                                    }
                                }
                                .addTo(controller)
                    }
                    bottomSheetDialog.show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO SAMPLE TEXT
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
            //TODO OPEN NOTIFICATION PAGE
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var state: String = String()
        var district: String = String()
        var pincode: String = String()
        var age: String = String()
        var dose: String = String()
        var districtCode: String = String()
    }
}