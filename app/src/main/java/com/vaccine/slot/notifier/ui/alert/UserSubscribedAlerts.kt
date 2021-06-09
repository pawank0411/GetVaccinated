package com.vaccine.slot.notifier.ui.alert

import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.ItemLayoutAlertsBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.dao.SubscribedSlotsDao
import com.vaccine.slot.notifier.data.model.SubscribeSlots
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom
import com.vaccine.slot.notifier.databinding.ActivityShowAlertsBinding
import com.vaccine.slot.notifier.other.Constants
import com.vaccine.slot.notifier.other.Constants.MAP_VACCINE
import com.vaccine.slot.notifier.other.Constants.UNSUBSCRIBE_DIALOG
import com.vaccine.slot.notifier.other.Status
import com.vaccine.slot.notifier.ui.base.BaseActivity
import com.vaccine.slot.notifier.ui.dialogs.ErrorMessageDialog
import com.vaccine.slot.notifier.ui.dialogs.UnsubscribeDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserSubscribedAlerts : BaseActivity() {

    private lateinit var activityShowAlertsBinding: ActivityShowAlertsBinding
    private lateinit var viewModel: UserSubscribedAlertViewModel

    @Inject
    lateinit var subscribedSlotsDao: SubscribedSlotsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.alter_list)
        }

        viewModel = ViewModelProvider(this).get(UserSubscribedAlertViewModel::class.java)
        activityShowAlertsBinding = ActivityShowAlertsBinding.inflate(layoutInflater)
        setContentView(activityShowAlertsBinding.root)

        activityShowAlertsBinding.progressBar.visibility = VISIBLE

        activityShowAlertsBinding.epoxy.layoutManager = LinearLayoutManager(this)
        subscribedSlotsDao.getAll()?.observe(this@UserSubscribedAlerts, {
            activityShowAlertsBinding.epoxy.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                override fun buildModels(controller: EpoxyController) {
                    if (it.isNullOrEmpty()) noMessages() else messagesAvailable()
                    it.forEachIndexed { index, alert ->
                        ItemLayoutAlertsBindingModel_()
                                .id(alert.id)
                                .customKey((index + 1).toString())
                                .district(alert.districtName)
                                .vaccine(if (alert.vaccineID?.size?.let { vc -> vc > 1 } == true) "--" else alert.vaccineID?.let { it1 -> getVaccineName(it1) })
                                .dose(alert.doseID?.get(0).toString())
                                .age(alert.age.toString() + "+")
                                .onClick { _ ->
                                    // unSubscribe here
                                    unSubscribeSlotsAlert(alert)
                                }
                                .addTo(controller)
                    }
                    activityShowAlertsBinding.progressBar.visibility = GONE
                }
            })
        })

        viewModel.slotUnSubscribeResponse.observe(this, { response ->
            when (response?.status) {
                Status.LOADING -> {
                    activityShowAlertsBinding.progressBar.visibility = VISIBLE
                }
                Status.SUCCESS -> {
                    viewModel.showDialog.observe(this, {
                        it?.getContentIfNotHandled()?.let {
                            deleteFromDb()
                        }
                    })
                }
                Status.ERROR -> {
                    activityShowAlertsBinding.progressBar.visibility = GONE
                    response.message?.let { showErrorMessage(it) }
                }
            }
        })
    }

    private fun deleteFromDb() {
        val subscribeSlots = viewModel.subscribeSlotsData.value

        subscribeSlots?.let {
            viewModel.deleteSlotAlert(subscribeSlots.id)
            showUnSubscribedDialog(resources.getString(R.string.unsubscribe_message,
                    subscribeSlots.districtName,
                    subscribeSlots.age.toString(),
                    subscribeSlots.doseID?.get(0),
                    if (subscribeSlots.vaccineID?.size?.let { vc -> vc > 1 } == true) "" else subscribeSlots.vaccineID?.let { getVaccineName(it) }))
            activityShowAlertsBinding.epoxy.requestModelBuild()
        } ?: showErrorMessage(resources.getString(R.string.error_message))
        activityShowAlertsBinding.progressBar.visibility = GONE
    }

    private fun getVaccineName(vaccineID: List<String>) = MAP_VACCINE.getValue(vaccineID[0])

    private fun showErrorMessage(message: String) {
        ErrorMessageDialog.newInstance(message).show(supportFragmentManager, Constants.ERROR_TAG)
    }

    private fun unSubscribeSlotsAlert(alert: SubscribedSlotsRoom) {
        val subscribedSlots = SubscribeSlots(
                "PLAYER_ID",
                alert.age,
                alert.stateID,
                alert.districtID,
                alert.vaccineID,
                alert.doseID
        )

        viewModel.getSlotUnSubscribeResponse(subscribedSlots)
        viewModel.setSubscribeSlotData(alert)
    }

    private fun noMessages() {
        activityShowAlertsBinding.emptyImage.visibility = VISIBLE
        activityShowAlertsBinding.emptyMessage.text =
                resources.getString(R.string.no_active_alerts)
        activityShowAlertsBinding.emptyMessage.visibility = VISIBLE
    }

    private fun messagesAvailable() {
        activityShowAlertsBinding.emptyImage.visibility = GONE
        activityShowAlertsBinding.emptyMessage.visibility = GONE
    }


    private fun showUnSubscribedDialog(message: String) {
        UnsubscribeDialog.newInstance(message).show(supportFragmentManager, UNSUBSCRIBE_DIALOG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}