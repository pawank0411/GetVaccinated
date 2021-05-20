package com.vaccine.slot.notifier.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vaccine.slot.notifier.BottomSheetBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityHomeBinding
import com.vaccine.slot.notifier.databinding.BottomSheetLayoutBinding
import com.vaccine.slot.notifier.databinding.ViewholderBottomSheetBinding
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

        viewModel = ViewModelProvider(this).get(HomeViewModel(this)::class.java)
        viewModel.getStateList()


        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)

        populateStateList()


        activityHomeBinding.stateNameEditText.setOnClickListener {
            // populates state list
            bottomSheetDialog.show()
        }

        activityHomeBinding.districtEditText.setOnClickListener {
            populateStateDistrictList() // populates stateDistrict list
            bottomSheetDialog.show()
        }

        viewModel.stateList.observe(this, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        viewModel.districtList.observe(this, {
            Log.d("DISTRICT", it.toString())
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        activityHomeBinding.checkAvailability.setOnClickListener {
            val intent = Intent(this, ShowSlots::class.java)
            intent.putExtra("STATE", activityHomeBinding.stateNameEditText.text)
            intent.putExtra("DISTRICT", activityHomeBinding.districtEditText.text)
            if (activityHomeBinding.age1.isChecked)
                intent.putExtra("AGE", activityHomeBinding.age1.text)
            else
                intent.putExtra("AGE", activityHomeBinding.age2.text)
            if (activityHomeBinding.dose1.isChecked)
                intent.putExtra("DOSE", activityHomeBinding.dose1.text)
            else
                intent.putExtra("DOSE", activityHomeBinding.dose2.text)
            startActivity(intent)
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
                        BottomSheetBindingModel_()
                            .id(i)
                            .name(i)
                            .onBind { _, view, _ ->
                                val binding = view.dataBinding as ViewholderBottomSheetBinding
                                binding.parentLayout.setOnClickListener {
                                    activityHomeBinding.stateNameEditText.setText(i)
                                    viewModel.getPreferredStateDistrict(i) // FETCHES DISTRICT OF PARTICULAR STATE
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
                        BottomSheetBindingModel_()
                            .id(i.code)
                            .name(i.name)
                            .onBind { _, view, _ ->
                                val binding = view.dataBinding as ViewholderBottomSheetBinding
                                binding.parentLayout.setOnClickListener {
                                    activityHomeBinding.districtEditText.setText(i.name)
                                    bottomSheetDialog.dismiss()
                                }
                            }
                            .addTo(controller)
                    }
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
}