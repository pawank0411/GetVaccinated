package com.vaccine.slot.notifier.repository

import android.util.Log
import com.vaccine.slot.notifier.other.Resource
import com.vaccine.slot.notifier.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
        private val apiService: ApiService,
//    private val notificationDao: NotificationDao
) {
    suspend fun getDataDetailsDistrictWise(code: Int) = withContext(Dispatchers.IO) {
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

    /* suspend fun addNotificationResponse(response: String?) {
         try {
             val notificationResponse = NotificationResponse(content = response)
             notificationDao.insert(notificationResponse)
         } catch (e: Exception) {
             println(e.localizedMessage)
         }
     }

     fun getAllNotificationResponse() = notificationDao.getAll()*/
}