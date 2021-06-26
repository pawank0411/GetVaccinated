package com.vaccine.slot.notifier.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.vaccine.slot.notifier.ItemLayoutWelcomeBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.ActivityWelcomeBinding
import com.vaccine.slot.notifier.other.PrefManager
import com.vaccine.slot.notifier.ui.home.HomeActivity
import com.worldsnas.slider.SliderModel_

class WelcomeActivity : AppCompatActivity() {

    private lateinit var activityWelcomeBinding: ActivityWelcomeBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWelcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(activityWelcomeBinding.root)

        prefManager = PrefManager(this)
        if (!prefManager.isFirstTimeLaunch) {
            launchHomeActivity()
            finish()
            return
        }
        prefManager.isFirstTimeLaunch = false

        val imageList = listOf(
            ContextCompat.getDrawable(
                this, R.drawable.welcome_image_1
            ),
            ContextCompat.getDrawable(
                this, R.drawable.welcome_image_2
            ),
            ContextCompat.getDrawable(
                this, R.drawable.welcome_image_3
            ),
            ContextCompat.getDrawable(
                this, R.drawable.welcome_image_4
            ),
            ContextCompat.getDrawable(
                this, R.drawable.welcome_image_5
            )
        )

        activityWelcomeBinding.epoxy.layoutManager = LinearLayoutManager(this)
        activityWelcomeBinding.epoxy.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val list = mutableListOf<EpoxyModel<*>>()
                imageList.forEachIndexed { index, image ->
                    list.add(
                        ItemLayoutWelcomeBindingModel_()
                            .id(index)
                            .drawable(image)
                    )
                }

                SliderModel_()
                    .id(list.hashCode())
                    .indicatorVisible(true)
                    .indicatorDotColor(getColor(R.color.blue_500))
                    .indicatorSelectedDotColor(getColor(R.color.yellow_200))
                    .models(list)
                    .addTo(controller)
            }
        })

        activityWelcomeBinding.finish.setOnClickListener {
            launchHomeActivity()
        }
    }

    private fun launchHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}