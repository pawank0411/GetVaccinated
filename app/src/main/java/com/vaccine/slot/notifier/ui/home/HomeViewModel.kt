package com.vaccine.slot.notifier.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaccine.slot.notifier.data.model.DistrictState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext
    context: Context
) : ViewModel() {
    private val _stateList = MutableLiveData<List<String>>()
    val stateList: LiveData<List<String>> get() = _stateList

    private val _districtList = MutableLiveData<List<DistrictState>>()
    val districtList: LiveData<List<DistrictState>> get() = _districtList

    private var jsonString: String? = null

    init {
        try {
            jsonString =
                context.assets.open("state_district.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun getStateList() {
        try {
            // TODO switch to Gson()
            val stateList = ArrayList<String>()
            if (jsonString != null) {
                val jsonState = JSONObject(jsonString!!)
                val jsonArray = jsonState.getJSONArray("states")
                for (i in 0 until jsonArray.length()) {
                    stateList.add(jsonArray.getJSONObject(i).getString("state_name"))
                }
            }
            _stateList.value = stateList
        } catch (e: Exception) {
            _stateList.value = listOf()
        }
    }

    fun getPreferredStateDistrict(state: String) {
        try {
            // TODO switch to Gson()
            val districtList = ArrayList<DistrictState>()
            if (jsonString != null) {
                val jsonState = JSONObject(jsonString!!)
                val jsonArray = jsonState.getJSONArray("states")
                for (i in 0 until jsonArray.length()) {
                    if (jsonArray.getJSONObject(i).getString("state_name").equals(state)) {
                        val districtArray = jsonArray.getJSONObject(i).getJSONArray("districts")
                        for (j in 0 until districtArray.length()) {
                            districtList.add(
                                    DistrictState(
                                            code = districtArray.getJSONObject(j).getInt("district_id"),
                                            name = districtArray.getJSONObject(j).getString("district_name")
                                    )
                            )
                        }
                    }
                }
            }
            _districtList.value = districtList
        } catch (e: Exception) {
            _districtList.value = listOf()
        }
    }
}