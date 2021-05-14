package com.vaccine.slot.notifier

import android.app.Application
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal


class NotificationHandler(private val application: Application) : OneSignal.NotificationOpenedHandler {
    /*
        HANDLE THE PUSH NOTIFICATION
     */
    override fun notificationOpened(result: OSNotificationOpenResult?) {
        result?.notification?.payload?.launchURL.let {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder
                .setShowTitle(true)
                .setToolbarColor(application.resources.getColor(R.color.purple_700))
                .build()
            customTabsIntent.launchUrl(application, Uri.parse(it))
        }
    }
}