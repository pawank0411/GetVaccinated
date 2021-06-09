package com.vaccine.slot.notifier.ui.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaccine.slot.notifier.BuildConfig
import com.vaccine.slot.notifier.dao.SubscribedSlotsDao
import com.vaccine.slot.notifier.data.model.SubscribeSlots
import com.vaccine.slot.notifier.data.model.response.SubscribeSlotsResponse
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom
import com.vaccine.slot.notifier.other.Event
import com.vaccine.slot.notifier.other.Resource
import com.vaccine.slot.notifier.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSubscribedAlertViewModel @Inject constructor(
        private val repository: Repository,
        private val subscribedSlotsDao: SubscribedSlotsDao
) : ViewModel() {

    private val _slotUnSubscribeResponse = MutableLiveData<Resource<SubscribeSlotsResponse>>()
    val slotUnSubscribeResponse: LiveData<Resource<SubscribeSlotsResponse>> get() = _slotUnSubscribeResponse

    private val _subscribeSlotsData = MutableLiveData<SubscribedSlotsRoom>()
    val subscribeSlotsData: LiveData<SubscribedSlotsRoom> get() = _subscribeSlotsData

    private val _showDialog = MutableLiveData<Event<Boolean>>()
    val showDialog: LiveData<Event<Boolean>> get() = _showDialog

    fun deleteSlotAlert(key: Int) {
        viewModelScope.launch {
            try {
                subscribedSlotsDao.deleteDataById(key)
                println("deleted")
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun getSlotUnSubscribeResponse(subscribeSlots: SubscribeSlots) {
        _slotUnSubscribeResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.setUnsubscribeForSlots(
                    subscribeSlots,
                    BuildConfig.DOMAIN + "/unsubscribe")
            _slotUnSubscribeResponse.postValue(response)
            _showDialog.value = Event(true)
        }
    }

    fun setSubscribeSlotData(subscribeSlots: SubscribedSlotsRoom) {
        _subscribeSlotsData.value = subscribeSlots
    }
}