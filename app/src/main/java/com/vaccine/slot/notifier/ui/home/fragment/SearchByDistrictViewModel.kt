package com.vaccine.slot.notifier.ui.home.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaccine.slot.notifier.data.model.District
import com.vaccine.slot.notifier.data.model.State
import com.vaccine.slot.notifier.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchByDistrictViewModel @Inject constructor(
        private val repository: Repository
) : ViewModel() {
    private val _stateList = MutableLiveData<List<State>>()
    val stateList: LiveData<List<State>> get() = _stateList

    private val _districtList = MutableLiveData<List<District>>()
    val districtList: LiveData<List<District>> get() = _districtList

    fun getStateList() {
        _stateList.value = repository.getStateListFromJSON()
    }

    fun getDistrictList(stateId: Int) {
        _districtList.value = repository.getDistrictList(stateId)
    }
}