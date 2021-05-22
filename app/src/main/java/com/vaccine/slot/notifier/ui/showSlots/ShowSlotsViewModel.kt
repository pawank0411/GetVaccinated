package com.vaccine.slot.notifier.ui.showSlots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaccine.slot.notifier.data.model.Date
import com.vaccine.slot.notifier.data.model.DistrictResponse
import com.vaccine.slot.notifier.other.Event
import com.vaccine.slot.notifier.other.Resource
import com.vaccine.slot.notifier.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ShowSlotsViewModel @Inject constructor(
        private val repository: Repository
) : ViewModel() {
    private val _slotDetails = MutableLiveData<Resource<DistrictResponse>>()
    val slotDetails: LiveData<Resource<DistrictResponse>> get() = _slotDetails

    private val _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> get() = _toast

    fun getSlotDetailsDistrictWise(code: Int) {
        _slotDetails.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getDataDetailsDistrictWise(code)
            _slotDetails.postValue(response)
        }
    }

    fun getSlotDetailsPinCodeWise(code: Int) {
        _slotDetails.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getDataDetailsPinCodeWise(code)
            _slotDetails.postValue(response)
        }
    }

    fun getSevenDayDate(): List<Date> {
        val sdf = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
        val list = ArrayList<Date>()
        for (i in 0..6) {
            val calendar: Calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day = sdf.format(calendar.time)
            val dateText = Date(date = day)
            list.add(dateText)
        }
        return list
    }
}