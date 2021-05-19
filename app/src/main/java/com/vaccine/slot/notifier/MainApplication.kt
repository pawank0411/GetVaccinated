package com.vaccine.slot.notifier

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.onesignal.OneSignal

const val APP_ID = "a3e7ac5f-d229-4cc3-a769-62261c9eaf24"
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // THEME FOLLOW SYSTEM -> DEVICE THEME
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
//        OneSignal.initWithContext(this)
//        OneSignal.setAppId(APP_ID)
    }
}