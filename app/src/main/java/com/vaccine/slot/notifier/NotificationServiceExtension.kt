package com.vaccine.slot.notifier

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getColor
import androidx.room.Room
import com.onesignal.OSMutableNotification
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.vaccine.slot.notifier.data.model.room.NotificationResponseRoom
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
        val responseData = notificationReceivedEvent?.notification?.additionalData

        try {
            val mutableNotification: OSMutableNotification? =
                notificationReceivedEvent?.notification?.mutableCopy()
            mutableNotification?.setExtender { builder: NotificationCompat.Builder ->
                builder.setColor(
                    getColor(context!!, R.color.blue_500)
                )
            }
            notificationReceivedEvent?.complete(mutableNotification)

            val db = Room.databaseBuilder(
                context!!,
                AppDatabase::class.java, "Database"
            ).build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val notificationResponse = NotificationResponseRoom(
                        content = response,
                        title = responseTitle,
                        time = ""
                    )
                    db.notificationDao().insert(notificationResponse)
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }
        } catch (e: Exception) {
            // notification will be sent normally
        }
    }
}