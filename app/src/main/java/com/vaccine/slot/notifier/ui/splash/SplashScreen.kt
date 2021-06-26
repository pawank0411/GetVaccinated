package com.vaccine.slot.notifier.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SplashTheme)

        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}