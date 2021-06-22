package com.vaccine.slot.notifier

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.onesignal.OSNotificationAction
import com.onesignal.OneSignal
import com.vaccine.slot.notifier.other.Constants.CO_WIN_LINK
import com.vaccine.slot.notifier.other.Constants.DONATE_LINK
import com.vaccine.slot.notifier.ui.notification.NotificationMessage
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
        OneSignal.setAppId(BuildConfig.APP_ID)
        OneSignal.setNotificationOpenedHandler { result ->
            val actionType = result?.action?.type

            val builder = CustomTabsIntent.Builder()
            val params = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(this, R.color.blue_700))
                    .build()
            builder.setShowTitle(true)
            builder.setDefaultColorSchemeParams(params)

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                when (result.action?.actionId) {
                    "id1" ->
                        // BOOK NOW
                        builder.build().launchUrl(
                                applicationContext,
                                Uri.parse(CO_WIN_LINK)
                        )
                    "id2" ->
                        // DONATE US
                        builder.build().launchUrl(
                                applicationContext,
                                Uri.parse(DONATE_LINK)
                        )
                }
            } else {
                startActivity(Intent(this, NotificationMessage::class.java))
            }
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
    }
}