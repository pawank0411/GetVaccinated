package com.vaccine.slot.notifier.ui.showSlots

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaccine.slot.notifier.data.model.Center
import com.vaccine.slot.notifier.data.model.Date
import com.vaccine.slot.notifier.data.model.Session
import com.vaccine.slot.notifier.data.model.response.ApiResponse
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
    private val _slotDetails = MutableLiveData<Resource<ApiResponse>>()
    val slotDetails: LiveData<Resource<ApiResponse>> get() = _slotDetails

    private val _chipFilterList = MutableLiveData<Set<String>>()
    val chipFilterList: LiveData<Set<String>> get() = _chipFilterList

    fun getSlotDetailsDistrictWise(code: Int) {
        _slotDetails.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getDataDetailsDistrictWise(code)
            _slotDetails.postValue(response)
        }
    }

    fun getSlotDetailsPinCodeWise(code: String) {
        _slotDetails.postValue(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.getDataDetailsPinCodeWise(code.toInt())
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

    fun backgroundColorStateList(
            checkedColor: Int = Color.parseColor("#022D83"),
            uncheckedColor: Int = Color.parseColor("#51C3C1C1"),
    ): ColorStateList {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
                checkedColor,
                uncheckedColor
        )
        return ColorStateList(states, colors)
    }

    fun textColorStateList(
            checkedColor: Int = Color.parseColor("#FFFFFFFF"),
            uncheckedColor: Int = Color.parseColor("#ADC6C4C4"),
    ): ColorStateList {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
                checkedColor,
                uncheckedColor
        )
        return ColorStateList(states, colors)
    }

    fun setChipFilterList(map: Map<Center, List<Session>?>?) {
        val chipSet = mutableSetOf<String>()
        map?.filterValues { it?.isNotEmpty() == true }?.map { item ->
            item.key.feeType?.let { it1 -> chipSet.add(it1.capitalizeWords()) }
            item.value?.forEach { session ->
                session.vaccine?.let { it2 -> chipSet.add(it2.capitalizeWords()) }
            }
        }
        _chipFilterList.value = chipSet
    }

    @SuppressLint("DefaultLocale")
    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.lowercase(
            Locale.getDefault()
        )
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}