package com.vaccine.slot.notifier.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.data.model.DistrictState
import com.vaccine.slot.notifier.databinding.ActivityHomeBinding
import com.vaccine.slot.notifier.databinding.BottomSheetLayoutBinding
import com.vaccine.slot.notifier.ui.showSlots.ShowSlots
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
    private lateinit var worker: WorkManager
    private lateinit var stateDistrictAdapter: BottomSheetAdapter
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

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)

        activityHomeBinding.stateNameEditText.setOnClickListener {
            // SHOWS STATE LIST
            stateList()
        }

        activityHomeBinding.districtEditText.setOnClickListener {
            // SHOWS STATE WISE DISTRICT LIST
            stateWiseDistrictList()
            bottomSheetDialog.show()
        }

        activityHomeBinding.checkAvailability.setOnClickListener {
            startActivity(
                    Intent(
                            this,
                            ShowSlots::class.java
                    )
            )
        }

        worker = WorkManager.getInstance()
    }

    private fun stateList() {
        /*
            FOR TESTING
            ************************************************
         */
        val item1 = DistrictState("Rajasthan")
        val item2 = DistrictState("Odisha")
        //**************************************************

        stateDistrictAdapter =
                BottomSheetAdapter(
                        this,
                        listOf(item1, item2),
                        object : BottomSheetAdapter.OnItemClickListener {
                            override fun onClick(name: String) {
                                bottomSheetDialog.dismiss()
                                activityHomeBinding.stateNameEditText.setText(name)
                            }
                        })
        bottomSheetLayoutBinding.dataList.adapter = stateDistrictAdapter
        bottomSheetLayoutBinding.dataList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bottomSheetDialog.show()
    }

    private fun stateWiseDistrictList() {
        /*
            FOR TESTING
            ************************************************
         */
        val item1 = DistrictState("Ajmer")
        val item2 = DistrictState("Jodhpur")
        val item3 = DistrictState("Nagaur")
        //**************************************************

        stateDistrictAdapter =
                BottomSheetAdapter(
                        this,
                        listOf(item1, item2, item3),
                        object : BottomSheetAdapter.OnItemClickListener {
                            override fun onClick(name: String) {
                                bottomSheetDialog.dismiss()
                                activityHomeBinding.districtEditText.setText(name)
                            }
                        })
        bottomSheetLayoutBinding.dataList.adapter = stateDistrictAdapter
        bottomSheetLayoutBinding.dataList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bottomSheetDialog.show()
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

    private fun runBackgroundService() {
        /*
            BACKGROUND SERVICE CODE
         */

        val taskData = Data.Builder().putString(MESSAGE_STATUS, "Notify Done.").build()

        val request =
                PeriodicWorkRequestBuilder<NotifyWorker>(
                        900000,
                        TimeUnit.MILLISECONDS,
                        300000,
                        TimeUnit.SECONDS
                )
                        .setInputData(taskData).build()
        worker.enqueue(request)

        worker.getWorkInfoByIdLiveData(request.id).observe(this, { workInfo ->
            workInfo.let {
                if (it.state.isFinished) {
                    val outputData = it.outputData
                    val taskResult = outputData.getString(NotifyWorker.WORK_RESULT)
                    Log.d("TASK_RESULT", taskResult.toString())
                } else {
                    val workStatus = workInfo.state
                    Log.d("WORK_RESULT", workStatus.toString())
                }
            }
        })
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }
}