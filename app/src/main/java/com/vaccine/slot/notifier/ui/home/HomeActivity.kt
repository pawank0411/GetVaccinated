package com.vaccine.slot.notifier.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityHomeBinding
import com.vaccine.slot.notifier.databinding.MessageDialogLayoutBinding
import com.vaccine.slot.notifier.ui.home.fragment.SearchByDistrictFragment.Companion.selectedDistrictName
import com.vaccine.slot.notifier.ui.home.fragment.SearchByDistrictFragment.Companion.selectedStateName
import com.vaccine.slot.notifier.ui.home.fragment.SearchByPinFragment.Companion.selectedPinCode
import com.vaccine.slot.notifier.ui.notification.NotificationMessage
import com.vaccine.slot.notifier.ui.showSlots.ShowSlots
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private val titles = arrayOf("Search By District", "Search By PIN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(false)
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(false)
            title = ""
        }

        activityHomeBinding.footerTextTitle.movementMethod = LinkMovementMethod.getInstance()
        activityHomeBinding.footerTextTitle.setLinkTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.blueF
                )
        )

        activityHomeBinding.viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(
                activityHomeBinding.tabs, activityHomeBinding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()

        activityHomeBinding.epoxyCarousel.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                // TODO Carousel
            }

        })

        activityHomeBinding.checkAvailability.setOnClickListener {
            when (activityHomeBinding.ageGroup.checkedRadioButtonId) {
                R.id.age1 ->
                    selectedAge = activityHomeBinding.age1.text.toString()
                R.id.age2 ->
                    selectedAge = activityHomeBinding.age2.text.toString()
            }

            when (activityHomeBinding.doseGroup.checkedRadioButtonId) {
                R.id.dose1 ->
                    selectedDose = activityHomeBinding.dose1.text.toString()
                R.id.dose2 ->
                    selectedDose = activityHomeBinding.dose2.text.toString()
            }

            if ((selectedStateName.isEmpty() || selectedDistrictName.isEmpty() || selectedAge.isEmpty() || selectedDose.isEmpty()) && selectedPinCode.isEmpty()) {
                val bottomSheetDialog = BottomSheetDialog(this)
                val messageDialogLayoutBinding = MessageDialogLayoutBinding.inflate(layoutInflater)
                bottomSheetDialog.setContentView(messageDialogLayoutBinding.root)

                messageDialogLayoutBinding.message.text = resources.getString(R.string.select_error)
                messageDialogLayoutBinding.close.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            } else
                startActivity(Intent(this, ShowSlots::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "SAMPLE TEXT"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
            return true
        } else if (id == R.id.action_notification) {
            startActivity(Intent(this@HomeActivity, NotificationMessage::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var selectedAge: String = String()
        var selectedDose: String = String()
    }
}