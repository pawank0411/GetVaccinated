package com.vaccine.slot.notifier.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.ItemLayoutMessageBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.dao.NotificationDao
import com.vaccine.slot.notifier.databinding.ActivityNotificationMessageBinding
import com.vaccine.slot.notifier.other.Constants.CO_WIN_LINK
import com.vaccine.slot.notifier.other.Constants.DELETE_DIALOG
import com.vaccine.slot.notifier.other.Constants.FEEDBACK_LINK
import com.vaccine.slot.notifier.ui.alert.UserSubscribedAlerts
import com.vaccine.slot.notifier.ui.base.BaseActivity
import com.vaccine.slot.notifier.ui.dialogs.DeleteDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_show_slots.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationMessage : BaseActivity() {

    @Inject
    lateinit var notificationDao: NotificationDao

    private lateinit var activityNotificationMessageBinding: ActivityNotificationMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationMessageBinding =
            ActivityNotificationMessageBinding.inflate(layoutInflater)
        setContentView(activityNotificationMessageBinding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.appBarTitleNotification)
        }

        activityNotificationMessageBinding.epoxy.layoutManager =
            LinearLayoutManager(this@NotificationMessage)

        notificationDao.getAll()?.observe(this@NotificationMessage, { response ->
            activityNotificationMessageBinding.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
                override fun buildModels(controller: EpoxyController) {
                    val sortedResponse = response?.reversed()
                    if (sortedResponse.isNullOrEmpty()) noMessages() else messagesAvailable()
                    sortedResponse?.forEach { message ->
                        ItemLayoutMessageBindingModel_()
                            .id(message.id)
                            .content(message.content)
                            .title(message.title)
                            .dateText(message.time)
                            .onClickBook { _ ->
                                openCustomWebsite(CO_WIN_LINK)
                            }
                            .addTo(controller)
                    }
                }
            })
        })

        activityNotificationMessageBinding.viewAlert.setOnClickListener {
            startActivity(Intent(this, UserSubscribedAlerts::class.java))
        }

        activityNotificationMessageBinding.raiseIssue.setOnClickListener {
            openCustomWebsite(FEEDBACK_LINK)
        }
    }

    private fun openCustomWebsite(link: String) {
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.blue_700))
            .build()
        builder.setShowTitle(true)
        builder.setDefaultColorSchemeParams(params)
        builder.build()
            .launchUrl(this, Uri.parse(link))
    }

    private fun noMessages() {

        activityNotificationMessageBinding.emptyImage.visibility = VISIBLE
        activityNotificationMessageBinding.emptyMessage.text =
            resources.getString(R.string.message_not_available)
        activityNotificationMessageBinding.emptyMessage.visibility = VISIBLE
    }

    private fun messagesAvailable() {
        activityNotificationMessageBinding.emptyImage.visibility = GONE
        activityNotificationMessageBinding.emptyMessage.visibility = GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notification, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        if (id == R.id.action_delete) {
            DeleteDialog().apply {
                setOnClickListener {
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            notificationDao.deleteAll()
                        }
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                    }
                }
            }.show(supportFragmentManager, DELETE_DIALOG)
        }
        return super.onOptionsItemSelected(item)
    }
}