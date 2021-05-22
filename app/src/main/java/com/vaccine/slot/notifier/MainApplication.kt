package com.vaccine.slot.notifier

import android.app.Application
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import com.onesignal.OSNotificationAction
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // THEME DARK
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId("a3e7ac5f-d229-4cc3-a769-62261c9eaf24")
        OneSignal.setNotificationOpenedHandler { result ->
            val actionType = result?.action?.type

            val builder = CustomTabsIntent.Builder()
            builder.setShowTitle(true)

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                when (result.action?.actionId) {
                    "id1" ->
                        // BOOK NOW
                        builder.build().launchUrl(
                                applicationContext,
                                Uri.parse("https://selfregistration.cowin.gov.in/")
                        )
                    "id2" ->
                        // DONATE US
                        builder.build().launchUrl(
                                applicationContext,
                                Uri.parse("https://www.instamojo.com/dashboard")
                        )
                }
            }
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
    }
}