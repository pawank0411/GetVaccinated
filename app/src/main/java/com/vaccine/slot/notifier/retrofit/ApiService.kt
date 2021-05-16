package com.vaccine.slot.notifier.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("calendarByDistrict")
    fun getSlotsDistrictWise(@Query("district_id") district_id: Int,
                             @Query("date") date: String): Call<ResponseBody>

    @GET("calendarByPin")
    fun getSlotsPincodeWise(@Query("pincode") pincode: Int,
                            @Query("date") date: String): Call<String>
}