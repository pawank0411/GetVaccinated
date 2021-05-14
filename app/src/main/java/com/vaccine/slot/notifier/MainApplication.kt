package com.vaccine.slot.notifier

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.onesignal.OneSignal

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // THEME FOLLOW SYSTEM -> DEVICE THEME
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .setNotificationOpenedHandler(NotificationHandler(this))
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}