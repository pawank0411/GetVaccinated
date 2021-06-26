package com.vaccine.slot.notifier.other

import android.content.Context
import android.content.SharedPreferences


class PrefManager(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0)
    private val editor: SharedPreferences.Editor = pref.edit()

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.apply()
        }

    companion object {
        private const val PREF_NAME = "getVaccinated-welcome"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }
}