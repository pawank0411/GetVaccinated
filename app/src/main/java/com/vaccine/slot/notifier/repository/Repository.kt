package com.vaccine.slot.notifier.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.vaccine.slot.notifier.data.model.*
import com.vaccine.slot.notifier.other.Resource
import com.vaccine.slot.notifier.retrofit.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class Repository @Inject constructor(
        @ApplicationContext
        context: Context,
        private val apiService: ApiService
) {
    private var jsonString: String? = null

    init {
        try {
            jsonString =
                    context.assets.open("state_district.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    suspend fun getDataDetailsDistrictWise(code: Int) =
            withContext(Dispatchers.IO) {
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())

                try {
                    val response = apiService.getSlotsDistrictWise(code, currentDate)
                    if (response.isSuccessful) {
                        Resource.success((response.body()))
                    } else {
                        Resource.error("Something went wrong. Please try again", null)
                    }
                } catch (e: Exception) {
                    Log.d("Exception", e.toString())
                    Resource.error("Something went wrong. Please try again", null)
                }
            }

    suspend fun getDataDetailsPinCodeWise(code: Int) = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        try {
            val response = apiService.getSlotsPinCodeWise(code, currentDate)
            if (response.isSuccessful) {
                Resource.success((response.body()))
            } else {
                Resource.error("Something went wrong. Please try again", null)
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            Resource.error("Something went wrong. Please try again", null)
        }
    }

    suspend fun setSubscribeUserForSlots(subscribeSlots: SubscribeSlots, url: String) =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.subscribeUserForSlot(subscribeSlots, url)
                    if (response.isSuccessful) {
                        Resource.success((response.body()))
                    } else {
                        Resource.error("Something went wrong. Please try again", null)
                    }
                } catch (e: Exception) {
                    Resource.error(e.toString(), null)
                }
            }

    suspend fun setUnsubscribeForSlots(subscribeSlots: SubscribeSlots, url: String) =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.unsubscribeUserForSlot(subscribeSlots, url)
                    if (response.isSuccessful) {
                        Resource.success((response.body()))
                    } else {
                        Resource.error("Something went wrong. Please try again", null)
                    }
                } catch (e: Exception) {
                    Resource.error(e.toString(), null)
                }
            }

    suspend fun reportSlotAlert(reportAlert: ReportAlert, url: String) =
            withContext(Dispatchers.IO) {
                try {
                    val response = apiService.reportSlot(reportAlert, url)
                    if (response.isSuccessful) {
                        Resource.success((response.body()))
                    } else {
                        Resource.error("Something went wrong. Please try again", null)
                    }
                } catch (e: Exception) {
                    Resource.error(e.toString(), null)
                }
            }

    suspend fun getInfo(url: String) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getInfo(url)
            if (response.isSuccessful) {
                Resource.success((response.body()))
            } else {
                Resource.error("Something went wrong. Please try again", null)
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            Resource.error("Something went wrong. Please try again", null)
        }
    }

    fun getStateListFromJSON(): List<State> {
        return Gson().fromJson(jsonString, StateList::class.java).states
    }

    fun getDistrictList(stateId: Int): List<District> {
        Gson().fromJson(jsonString, StateList::class.java).states.forEach { state ->
            if (state.stateId == stateId)
                return state.districts
        }
        return listOf()
    }
}