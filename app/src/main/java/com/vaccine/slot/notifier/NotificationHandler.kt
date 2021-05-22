package com.vaccine.slot.notifier

import android.app.Application
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal

class NotificationHandler(private val application: Application) : OneSignal.OSNotificationOpenedHandler {
    /*
      HANDLE THE PUSH NOTIFICATION
   */
    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        val actionType = result?.action?.type

        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            when (result.action?.actionId) {
                "id1" ->
                    // BOOK NOW
                    builder.build().launchUrl(application, Uri.parse("https://selfregistration.cowin.gov.in/"))
                "id2" ->
                    // DONATE US
                    builder.build().launchUrl(application, Uri.parse("https://www.instamojo.com/dashboard"))
            }
        }
    }
}