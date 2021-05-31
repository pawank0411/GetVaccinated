package com.vaccine.slot.notifier.ui.notification

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.ItemLayoutMessageBindingModel_
import com.vaccine.slot.notifier.dao.NotificationDao
import com.vaccine.slot.notifier.databinding.ActivityNotificationMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationMessage : AppCompatActivity() {
    @Inject
    lateinit var notificationDao: NotificationDao

    private lateinit var activityNotificationMessageBinding: ActivityNotificationMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationMessageBinding = ActivityNotificationMessageBinding.inflate(layoutInflater)
        setContentView(activityNotificationMessageBinding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
            title = "Messages"
        }

        activityNotificationMessageBinding.epoxy.layoutManager = LinearLayoutManager(this@NotificationMessage)
        notificationDao.getAll()?.observe(this@NotificationMessage, { response ->
            activityNotificationMessageBinding.epoxy.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
                override fun buildModels(controller: EpoxyController) {
                    val sortedResponse = response?.reversed()
                    sortedResponse?.forEach { message ->
                        ItemLayoutMessageBindingModel_()
                                .id(message.id)
                                .content(message.content)
                                .title(message.title)
                                .onClickBook { _ ->
                                    openCoWinWebsite()
                                }
                                .onClickStop { _ ->
                                    // TODO stop alerts
                                }
                                .addTo(controller)
                    }
                }
            })
        })
    }

    private fun openCoWinWebsite() {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.build().launchUrl(this, Uri.parse("https://selfregistration.cowin.gov.in/"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return true
    }
}