package com.vaccine.slot.notifier

import android.app.Application
import android.content.Context
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal


class NotificationHandler(private val application: Application) :
    OneSignal.OSRemoteNotificationReceivedHandler, OneSignal.OSNotificationOpenedHandler {
    /*
      HANDLE THE PUSH NOTIFICATION
   */
    override fun remoteNotificationReceived(
        context: Context?,
        notificationReceivedEvent: OSNotificationReceivedEvent?
    ) {
        TODO("Not yet implemented")
    }

    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        TODO("Not yet implemented")
    }
}