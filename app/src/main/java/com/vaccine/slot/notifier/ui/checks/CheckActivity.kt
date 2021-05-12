package com.vaccine.slot.notifier.ui.checks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vaccine.slot.notifier.databinding.ActivityChecksBinding
import com.vaccine.slot.notifier.ui.home.HomeActivity

class CheckActivity : AppCompatActivity() {

    private lateinit var activityChecksBinding: ActivityChecksBinding
    private lateinit var powerManager: PowerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityChecksBinding = ActivityChecksBinding.inflate(layoutInflater)
        setContentView(activityChecksBinding.root)

        powerManager = getSystemService(POWER_SERVICE) as PowerManager

        activityChecksBinding.checkSubTitle.setOnClickListener {
            checkBatteryOptimization()
        }

        activityChecksBinding.proceedButton.setOnClickListener {
            // TODO to be transferred in viewModel
            if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Please allow all the permissions", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkBatteryOptimization() {
        /*
           BATTERY OPTIMIZATION CODE
        */
        val intent = Intent()
        val packageName = packageName
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (powerManager.isIgnoringBatteryOptimizations(packageName))
            startActivity(Intent(this, HomeActivity::class.java))
    }
}