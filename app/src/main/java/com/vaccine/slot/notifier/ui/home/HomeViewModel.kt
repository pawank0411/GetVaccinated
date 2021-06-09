package com.vaccine.slot.notifier.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaccine.slot.notifier.BuildConfig
import com.vaccine.slot.notifier.dao.ReportDao
import com.vaccine.slot.notifier.dao.SubscribedSlotsDao
import com.vaccine.slot.notifier.dao.UserIDDao
import com.vaccine.slot.notifier.data.model.*
import com.vaccine.slot.notifier.data.model.response.ReportAlertResponse
import com.vaccine.slot.notifier.data.model.response.SubscribeSlotsResponse
import com.vaccine.slot.notifier.data.model.room.ReportAlertRoom
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom
import com.vaccine.slot.notifier.data.model.room.UserID
import com.vaccine.slot.notifier.other.Event
import com.vaccine.slot.notifier.other.Resource
import com.vaccine.slot.notifier.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
        private val repository: Repository,
        private val subscribedSlotsDao: SubscribedSlotsDao,
        private val reportDao: ReportDao,
        private val userIDDao: UserIDDao
) : ViewModel() {
    private val _stateList = MutableLiveData<List<State>>()
    val stateList: LiveData<List<State>> get() = _stateList

    private val _districtList = MutableLiveData<List<District>>()
    val districtList: LiveData<List<District>> get() = _districtList

    private val _currentTabSelection = MutableLiveData<Int>()
    val tabSelection: LiveData<Int> get() = _currentTabSelection

    private val _contentTabDistrict = MutableLiveData<List<String>>()
    val contentTabDistrict: LiveData<List<String>> get() = _contentTabDistrict

    private val _contentTabPincode = MutableLiveData<List<String>>()
    val contentTabPincode: LiveData<List<String>> get() = _contentTabPincode

    private val _slotSubscribeResponse = MutableLiveData<Resource<SubscribeSlotsResponse>>()
    val slotSubscribeResponse: LiveData<Resource<SubscribeSlotsResponse>> get() = _slotSubscribeResponse

    private val _reportAlertResponse = MutableLiveData<Resource<ReportAlertResponse>>()
    val reportAlertResponse: LiveData<Resource<ReportAlertResponse>> get() = _reportAlertResponse

    private val _subscribeSlotsData = MutableLiveData<SubscribeSlots>()
    val subscribeSlotsData: LiveData<SubscribeSlots> get() = _subscribeSlotsData

    private val _getInfo = MutableLiveData<Resource<Info>>()
    val getInfo: LiveData<Resource<Info>> get() = _getInfo

    private val _showDialog = MutableLiveData<Event<Boolean>>()
    val showDialog: LiveData<Event<Boolean>> get() = _showDialog

    val userText = MutableLiveData<String>()

    init {
        _currentTabSelection.value = 0
        _contentTabDistrict.value = listOf("State", "District")

        _getInfo.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getInfo(BuildConfig.DOMAIN + "/info")
            _getInfo.postValue(response)
        }

        _stateList.value = repository.getStateListFromJSON()
    }

    fun setTabSelected(pos: Int) {
        _currentTabSelection.value = pos
    }

    fun getDistrictList(stateId: Int) {
        _districtList.value = repository.getDistrictList(stateId)
    }

    fun getContentList(title: String) {
        if (title.contains("District"))
            _contentTabDistrict.value = listOf("State", "District")
        else
            _contentTabPincode.value = listOf("Pincode")

    }

    fun getSlotsSubscribeResponse(subscribeSlots: SubscribeSlots) {
        _slotSubscribeResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.setSubscribeUserForSlots(
                    subscribeSlots,
                    BuildConfig.DOMAIN + "/subscribe"
            )
            _slotSubscribeResponse.postValue(response)
            _showDialog.value = Event(true)
        }
    }

    fun insertSubscribedUserInDb(subscribeSlots: SubscribeSlots, selectedStateName: String, selectedDistrictName: String) {
        viewModelScope.launch {
            try {
                val userSubscribedSlots = SubscribedSlotsRoom(
                        clientID = subscribeSlots.clientId,
                        age = subscribeSlots.preferredAge,
                        stateID = subscribeSlots.preferredStateID,
                        stateName = selectedStateName,
                        districtID = subscribeSlots.preferredDistrictID,
                        districtName = selectedDistrictName,
                        vaccineID = subscribeSlots.preferredVaccineID,
                        doseID = subscribeSlots.preferredDoseID
                )
                subscribedSlotsDao.insert(userSubscribedSlots)
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun getReportAreaResponse(reportAlert: ReportAlert) {
        _reportAlertResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.reportSlotAlert(reportAlert, BuildConfig.DOMAIN + "/report")
            _reportAlertResponse.postValue(response)
            _showDialog.value = Event(true)
        }
    }

    fun insertReportSlotInDb(reportAlert: ReportAlert, selectedStateName: String, selectedDistrictName: String) {
        viewModelScope.launch {
            try {
                val reportAlertRoom = ReportAlertRoom(
                        age = reportAlert.age,
                        stateID = reportAlert.stateID,
                        stateName = selectedStateName,
                        districtID = reportAlert.districtID,
                        districtName = selectedDistrictName
                )
                reportDao.insert(reportAlertRoom)
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun insertPlayerIDInDb(userID: UserID) {
        viewModelScope.launch {
            try {
                userIDDao.insert(userID)
                println("inserted")
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun setSubscribeSlotData(subscribeSlots: SubscribeSlots) {
        _subscribeSlotsData.value = subscribeSlots
    }
}