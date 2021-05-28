package com.vaccine.slot.notifier.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaccine.slot.notifier.data.model.ContentTab
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

    private val _contentTab = MutableLiveData<List<ContentTab>>()
    val contentTab: LiveData<List<ContentTab>> get() = _contentTab

    init {
        _currentTabSelection.value = 0
        _contentTab.value = listOf(
            ContentTab(
                "State",
                false,
                focusTouch = false,
                showImage = true
            ),
            ContentTab(
                "District",
                false,
                focusTouch = false,
                showImage = true
            )
        )
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
        val contentItems = ArrayList<ContentTab>()
        if (title.contains("District")) {
            contentItems.add(
                ContentTab(
                    "State",
                    false,
                    focusTouch = false,
                    showImage = true
                )
            )
            contentItems.add(
                ContentTab(
                    "District",
                    false,
                    focusTouch = false,
                    showImage = true
                )
            )
        } else {
            contentItems.add(
                ContentTab(
                    "Pincode",
                    true,
                    focusTouch = true,
                    showImage = false
                )
            )
        }

        _contentTab.value = contentItems
    }
}