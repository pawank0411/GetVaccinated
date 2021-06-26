package com.vaccine.slot.notifier.ui.home

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import com.vaccine.slot.notifier.*
import com.vaccine.slot.notifier.dao.SubscribedSlotsDao
import com.vaccine.slot.notifier.dao.UserIDDao
import com.vaccine.slot.notifier.data.model.ReportAlert
import com.vaccine.slot.notifier.data.model.SubscribeSlots
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom
import com.vaccine.slot.notifier.data.model.room.UserID
import com.vaccine.slot.notifier.databinding.*
import com.vaccine.slot.notifier.other.*
import com.vaccine.slot.notifier.other.Constants.ACTION_INSTALL
import com.vaccine.slot.notifier.other.Constants.ACTION_NOTIFICATION
import com.vaccine.slot.notifier.other.Constants.CONNECTION_DIALOG
import com.vaccine.slot.notifier.other.Constants.CRITERIA_TAG
import com.vaccine.slot.notifier.other.Constants.DISTRICT_TAG
import com.vaccine.slot.notifier.other.Constants.ERROR_TAG
import com.vaccine.slot.notifier.other.Constants.SNACK_DIALOG
import com.vaccine.slot.notifier.other.Constants.STATE_TAG
import com.vaccine.slot.notifier.other.Constants.SUBSCRIBE_DIALOG
import com.vaccine.slot.notifier.other.Constants.SUCCESS_SUBSCRIBED
import com.vaccine.slot.notifier.other.Constants.TAB_SEARCH_BY_DISTRICT
import com.vaccine.slot.notifier.other.Constants.TAB_SEARCH_BY_PIN_CODE
import com.vaccine.slot.notifier.other.Constants.UPDATED_APK_FILE_NAME
import com.vaccine.slot.notifier.ui.base.BaseActivity
import com.vaccine.slot.notifier.ui.dialogs.*
import com.vaccine.slot.notifier.ui.notification.NotificationMessage
import com.vaccine.slot.notifier.ui.showSlots.ShowSlots
import com.worldsnas.slider.SliderModel_
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_criteria_alert_dialog.*
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity(), OSSubscriptionObserver {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val titles = listOf("Search By District", "Search By Pin")

    @Inject
    lateinit var subscribedSlotsDao: SubscribedSlotsDao

    @Inject
    lateinit var userIDDao: UserIDDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        activityHomeBinding.toolbarLayout.apply {
            title = resources.getString(R.string.app_name)
            elevation = 0.0F
        }

        firebaseAnalytics = Firebase.analytics

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        OneSignal.addSubscriptionObserver(this@HomeActivity)

        userIDDao.getPlayerID().observe(this, { userID ->
            userID?.let {
                playerID = it.playerID
            }
        })

        // listen the completion of downloading updates
        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                showBottomSnack(resources.getString(R.string.update_app), ACTION_INSTALL)
                unregisterReceiver(this)
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        activityHomeBinding.contentHome.epoxy.layoutManager =
                LinearLayoutManager(this)
        activityHomeBinding.contentHome.epoxy.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

                val photoList = mutableListOf<EpoxyModel<*>>()
                val urlList = homeViewModel.getInfo.value?.data?.imagesLink

                if (!isInternetAvailable()) NoConnectionDialog().show(supportFragmentManager, CONNECTION_DIALOG) else
                    urlList?.forEach {
                        photoList.add(ItemLayoutViewPagerBindingModel_()
                                .id(Random().nextInt())
                                .onBind { _, view, _ ->
                                    val binding = view.dataBinding as ViewholderItemLayoutViewPagerBinding
                                    Picasso
                                            .get()
                                            .load(Uri.parse(it))
                                            .placeholder(
                                                    ContextCompat.getDrawable(
                                                            this@HomeActivity,
                                                            R.drawable.ic_image_outline
                                                    )!!
                                            )
                                            .error(
                                                    ContextCompat.getDrawable(
                                                            this@HomeActivity,
                                                        R.drawable.ic_image_broken_variant
                                                    )!!
                                            )
                                            .into(binding.image)
                                }
                        )
                    }

                SliderModel_()
                        .id("carousel")
                        .models(photoList)
                        .indicatorVisible(false)
                        .addTo(controller)

                val subItems = mutableListOf<EpoxyModel<*>>()
                titles.forEach { title ->
                    val backgroundColor = when (homeViewModel.tabSelection.value) {
                        0 -> {
                            if (title.contains(TAB_SEARCH_BY_DISTRICT)) {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.tab_rounded_corner
                                )
                            } else {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.transparent_background
                                )
                            }
                        }
                        1 -> {
                            if (title.contains(TAB_SEARCH_BY_PIN_CODE)) {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                    R.drawable.tab_rounded_corner
                                )
                            } else {
                                ContextCompat.getDrawable(
                                        this@HomeActivity,
                                        R.drawable.transparent_background
                                )
                            }
                        }
                        else -> ContextCompat.getDrawable(
                                this@HomeActivity,
                                R.drawable.transparent_background
                        )
                    }
                    subItems.add(ItemLayoutTabBindingModel_()
                            .id(Random().nextInt())
                            .text(title)
                            .background(backgroundColor)
                            .onClick { _ ->
                                homeViewModel.setTabSelected(if (title.contains(TAB_SEARCH_BY_DISTRICT)) 0 else 1)
                                homeViewModel.getContentList(title)
                            })
                }

                GridTabModel_()
                        .id(subItems.hashCode())
                        .models(subItems)
                        .addTo(controller)

                when (homeViewModel.tabSelection.value) {
                    0 -> {
                        activityHomeBinding.contentHome.notifySlots.visibility = VISIBLE
                        val hintTextList = homeViewModel.contentTabDistrict.value
                        for (i in 0..1) {
                            ItemLayoutDistrictBindingModel_()
                                    .id(i)
                                    .hintText(hintTextList?.get(i))
                                    .text(if (i == 0) selectedStateName else selectedDistrictName)
                                    .onClick { _ ->
                                        when (i) {
                                            0 -> {
                                                StateDialog().apply {
                                                    setOnClickListener { state, id ->
                                                        setStateInputLayout(state, id)
                                                    }
                                                }.show(supportFragmentManager, STATE_TAG)
                                            }
                                            1 -> {
                                                if (selectedStateName.isNotEmpty()) {
                                                    DistrictDialog().apply {
                                                        setOnClickListener { district, id ->
                                                            setDistrictInputLayout(district, id)
                                                        }
                                                    }.show(supportFragmentManager, DISTRICT_TAG)
                                                }
                                            }
                                        }
                                    }
                                    .addTo(controller)
                        }
                    }
                    1 -> {
                        activityHomeBinding.contentHome.notifySlots.visibility = GONE
                        val hintTextList = homeViewModel.contentTabPincode.value
                        ItemLayoutPincodeBindingModel_()
                                .id(Random().nextInt())
                                .hintText(hintTextList?.get(0))
                                .viewModel(homeViewModel)
                                .addTo(controller)
                    }
                }
            }
        })

        activityHomeBinding.contentHome.notifySlots.setOnClickListener {
            selectedAge = getUserSelectedAge()
            if (!isInternetAvailable()) NoConnectionDialog().show(supportFragmentManager, CONNECTION_DIALOG) else
                if (selectedDistrictName.isEmpty()) showErrorMessage(resources.getString(R.string.select_error))
                else {
                    val isNotificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
                    if (isNotificationEnabled) {
                        CriteriaDialog().apply {
                            setOnClickListener { dose, vaccine ->
                                setUserToSubscribe(dose, vaccine)
                            }
                        }.show(supportFragmentManager, CRITERIA_TAG)
                    } else {
                        showBottomSnack(resources.getString(R.string.allow_notification), ACTION_NOTIFICATION)
                    }
                }
        }

        activityHomeBinding.contentHome.checkAvailability.setOnClickListener {
            selectedAge = getUserSelectedAge()
            selectedDose = getUserSelectedDose()

            if (!isInternetAvailable()) NoConnectionDialog().show(supportFragmentManager, CONNECTION_DIALOG) else
                if (homeViewModel.tabSelection.value == 0 && (selectedStateName.isEmpty() || selectedDistrictName.isEmpty())) {
                    showErrorMessage(resources.getString(R.string.select_error))
                } else if (homeViewModel.tabSelection.value == 1 && (selectedPinCode.isEmpty() || selectedPinCode.length < 6)) {
                    showErrorMessage(resources.getString(R.string.pincode_error))
                } else {
                    startActivity(Intent(this, ShowSlots::class.java))
                }
        }

        homeViewModel.userText.observe(this, {
            selectedPinCode = it
        })

        homeViewModel.getInfo.observe(this, {
            activityHomeBinding.contentHome.epoxy.requestModelBuild()
            val currentVersion = BuildConfig.VERSION_CODE
            val updatedVersion = it.data?.version?.toInt()
            if (updatedVersion != null) {
                if (updatedVersion > currentVersion)
                    if (!isUpdatedFileExists()) it.data.downloadLink.let { it1 -> downloadUpdate(it1) }
                    else {
                        homeViewModel.showDialog.observe(this, { ev ->
                            ev?.getContentIfNotHandled()?.let {
                                showBottomSnack(resources.getString(R.string.update_app), ACTION_INSTALL)
                            }
                        })
                    }
            }
        })

        homeViewModel.tabSelection.observe(this, {
            selectedTab = it.toString()
            activityHomeBinding.contentHome.epoxy.requestModelBuild()
        })

        homeViewModel.slotSubscribeResponse.observe(this@HomeActivity, { response ->
            firebaseAnalytics.logEvent("ResponseSubscribe") {
                param("response", response.toString())
                param("message", response.message.toString())
                param("data", response.data.toString())
            }
            when (response?.status) {
                Status.LOADING -> {
                    activityHomeBinding.contentHome.progressBar.visibility = VISIBLE
                }
                Status.SUCCESS -> {
                    homeViewModel.showDialog.observe(this, {
                        it?.getContentIfNotHandled()?.let {
                            if (response.data?.message?.contains(
                                            SUCCESS_SUBSCRIBED,
                                            ignoreCase = true
                                    ) == true
                            ) insertToDb() else reportForArea()
                        }
                    })
                }
                Status.ERROR -> {
                    showErrorMessage(resources.getString(R.string.error_message))
                }
            }
        })

        subscribedSlotsDao.getAll()?.observe(this, {
            storedData = it
        })

        homeViewModel.reportAlertResponse.observe(this, { response ->
            when (response?.status) {
                Status.LOADING -> {
                    activityHomeBinding.contentHome.progressBar.visibility = VISIBLE
                }
                Status.SUCCESS -> {
                    homeViewModel.showDialog.observe(this, {
                        it?.getContentIfNotHandled()?.let {
                            if (response.data?.status == true) openSubscribeDialog(
                                    resources.getString(R.string.happy_serve),
                                    resources.getString(R.string.first_user)
                            ) else showErrorMessage(resources.getString(R.string.error_message))
                        }
                    })
                }
                Status.ERROR -> {
                    showErrorMessage(resources.getString(R.string.error_message))
                }
            }
        })

        activityHomeBinding.contentHome.footerTextTitle.movementMethod = LinkMovementMethod.getInstance()

        if (savedInstanceState != null) {
            val stateDialog =
                    supportFragmentManager.findFragmentByTag(STATE_TAG) as StateDialog?
            stateDialog?.setOnClickListener { s, i ->
                setStateInputLayout(s, i)
            }
            val districtDialog =
                    supportFragmentManager.findFragmentByTag(DISTRICT_TAG) as DistrictDialog?
            districtDialog?.setOnClickListener { s, i ->
                setDistrictInputLayout(s, i)
            }
            val criteriaDialog =
                    supportFragmentManager.findFragmentByTag(CRITERIA_TAG) as CriteriaDialog?
            criteriaDialog?.setOnClickListener { s1, s2 ->
                setUserToSubscribe(s1, s2)
            }
            val snackBarDialog =
                    supportFragmentManager.findFragmentByTag(SNACK_DIALOG) as BottomSnackBarDialog?
            snackBarDialog?.setOnClickListener { s ->
                setActionIntent(s)
            }
        }
    }

    private fun reportForArea() {

        val reportAlert = ReportAlert(
                age = selectedAge,
                stateID = selectedStateId.toString(),
                districtID = selectedDistrictCodeId
        )
        homeViewModel.getReportAreaResponse(reportAlert)
    }

    private fun insertToDb() {

        val subscribeSlots = homeViewModel.subscribeSlotsData.value
        subscribeSlots?.let { data ->
            homeViewModel.insertSubscribedUserInDb(
                    data,
                    selectedStateName,
                    selectedDistrictName
            )
            openSubscribeDialog(
                    resources.getString(R.string.notification_set),
                    resources.getString(R.string.success_message)
            )
        } ?: showErrorMessage(resources.getString(R.string.error_message))
    }

    private fun setDistrictInputLayout(district: String, id: Int) {
        selectedDistrictName = district
        selectedDistrictCodeId = id
        activityHomeBinding.contentHome.epoxy.requestModelBuild()
    }

    private fun setStateInputLayout(state: String, id: Int) {
        selectedStateName = state
        selectedDistrictName = ""
        selectedStateId = id
        homeViewModel.getDistrictList(id)
        activityHomeBinding.contentHome.epoxy.requestModelBuild()
    }

    private fun setUserToSubscribe(doseID: List<String>?, vaccineID: List<String>?) {

        if (playerID.isNullOrEmpty()) {
            showErrorMessage(resources.getString(R.string.error_message))
            return
        }

        val subscribeSlots = SubscribeSlots(
                playerID,
                selectedAge,
                selectedStateId?.toString(),
                selectedDistrictCodeId,
                vaccineID,
                doseID
        )

        val isExists = isEqual(doseID, vaccineID)
        if (isExists) {
            showErrorMessage(resources.getString(R.string.user_subscribed))
            return
        }
        homeViewModel.getSlotsSubscribeResponse(subscribeSlots) // make the user to subscribe for the particular district
        homeViewModel.setSubscribeSlotData(subscribeSlots)
    }

    private fun isEqual(dose: List<String>?, second: List<String>?): Boolean {

        val first = mutableListOf<String>()
        storedData.forEach { data ->
            if (data.districtID == selectedDistrictCodeId &&
                    data.age == selectedAge &&
                    data.doseID?.get(0) == dose?.get(0)
            ) {
                data.vaccineID?.forEach {
                    first.add(it)
                }
            }
        }

        first.forEach { firstValue ->
            second?.forEach { secondValue ->
                if (firstValue == secondValue) {
                    return true
                }
            }
        }
        return false
    }

    private fun getUserSelectedAge(): Int =
            when (activityHomeBinding.contentHome.ageGroup.checkedRadioButtonId) {
                R.id.age1 ->
                    activityHomeBinding.contentHome.age1.text.toString().split("[–+]".toRegex())
                            .map { it.trim() }[0].toInt()
                R.id.age2 ->
                    activityHomeBinding.contentHome.age2.text.toString().split("[–+]".toRegex())
                            .map { it.trim() }[0].toInt()
                else -> 0
            }


    private fun getUserSelectedDose(): String {
        when (activityHomeBinding.contentHome.doseGroup.checkedRadioButtonId) {
            R.id.dose1 ->
                selectedDose = activityHomeBinding.contentHome.dose1.text.toString()
            R.id.dose2 ->
                selectedDose = activityHomeBinding.contentHome.dose2.text.toString()
        }
        return selectedDose.split(" ")[1]
    }

    private fun isUpdatedFileExists(): Boolean {
        var destination: String = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        val fileName = UPDATED_APK_FILE_NAME
        destination += fileName
        fileDestination = destination

        val file = File(destination)
        if (file.exists())
            return true
        return false
    }

    private fun openSubscribeDialog(title: String, message: String) {
        activityHomeBinding.contentHome.progressBar.visibility = GONE
        SubscribeDialog.newInstance(title, message).show(supportFragmentManager, SUBSCRIBE_DIALOG)
    }

    private fun showErrorMessage(message: String) {
        activityHomeBinding.contentHome.progressBar.visibility = GONE
        ErrorMessageDialog.newInstance(message).show(supportFragmentManager, ERROR_TAG)
    }

    private fun showBottomSnack(message: String, action: String) {

        BottomSnackBarDialog.newInstance(message, action).apply {
            setOnClickListener {
                setActionIntent(it)
            }
        }.show(supportFragmentManager, SNACK_DIALOG)
    }

    private fun setActionIntent(action: String) {
        when (action) {
            ACTION_NOTIFICATION -> {
                startActivity(Intent()
                        .setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                        .putExtra("app_package", packageName)
                        .putExtra("app_uid", applicationInfo.uid)
                        .putExtra("android.provider.extra.APP_PACKAGE", packageName))

            }
            ACTION_INSTALL -> {
                val contentUri: Uri = FileProvider.getUriForFile(this@HomeActivity, BuildConfig.APPLICATION_ID + ".provider", File(fileDestination))
                val openFileIntent = Intent(Intent.ACTION_VIEW)
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                openFileIntent.data = contentUri
                startActivity(openFileIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
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
        var selectedAge: Int? = 0
        var selectedDose: String = String()
        var selectedTab: String = String()
        var selectedPinCode: String = String()
        var selectedStateName: String = String()
        var selectedDistrictName: String = String()
        var selectedDistrictCodeId: Int = 0
        var selectedStateId: Int? = 0
        var storedData: List<SubscribedSlotsRoom> = listOf()
        var playerID: String? = String()
        var fileDestination: String = String()
    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges?) {
        if (stateChanges?.from?.isSubscribed != true &&
                stateChanges?.to?.isSubscribed == true) {
            homeViewModel.insertPlayerIDInDb(UserID(id = 0, playerID = stateChanges.to.userId))
        }
    }
}