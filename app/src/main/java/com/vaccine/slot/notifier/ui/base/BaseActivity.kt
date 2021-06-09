package com.vaccine.slot.notifier.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.vaccine.slot.notifier.helper.NetworkHelper
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var networkHelper: NetworkHelper
    fun isInternetAvailable(): Boolean {
        return networkHelper.isNetworkConnected()
    }
}