package com.vaccine.slot.notifier.retrofit

import com.vaccine.slot.notifier.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("calendarByDistrict")
    suspend fun getSlotsDistrictWise(@Query("district_id") district_id: Int,
                                     @Query("date") date: String): Response<ApiResponse>

    @GET("calendarByPin")
    suspend fun getSlotsPinCodeWise(@Query("pincode") pincode: Int,
                                    @Query("date") date: String): Response<ApiResponse>
}