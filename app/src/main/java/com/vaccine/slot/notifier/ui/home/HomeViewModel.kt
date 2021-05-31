package com.vaccine.slot.notifier.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaccine.slot.notifier.data.model.District
import com.vaccine.slot.notifier.data.model.State
import com.vaccine.slot.notifier.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val repository: Repository
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

    val userText = MutableLiveData<String>()

    init {
        _currentTabSelection.value = 0
        _contentTabDistrict.value = listOf("State", "District")
    }

    fun setTabSelected(pos: Int) {
        _currentTabSelection.value = pos
    }

    fun getStateList() {
        _stateList.value = repository.getStateListFromJSON()
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
}