package com.vaccine.slot.notifier

import android.content.Context
import androidx.room.Room
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.vaccine.slot.notifier.data.model.NotificationResponse
import com.vaccine.slot.notifier.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationServiceExtension : OneSignal.OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(
            context: Context?,
            notificationReceivedEvent: OSNotificationReceivedEvent?
    ) {
        val response = notificationReceivedEvent?.notification?.body
        val responseTitle = notificationReceivedEvent?.notification?.title

        val db = Room.databaseBuilder(
                context!!,
                AppDatabase::class.java, "Notification Database"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationResponse = NotificationResponse(content = response, title = responseTitle)
                db.notificationDao().insert(notificationResponse)
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }
}