package com.vaccine.slot.notifier.retrofit

import com.vaccine.slot.notifier.data.model.Info
import com.vaccine.slot.notifier.data.model.ReportAlert
import com.vaccine.slot.notifier.data.model.SubscribeSlots
import com.vaccine.slot.notifier.data.model.response.ApiResponse
import com.vaccine.slot.notifier.data.model.response.ReportAlertResponse
import com.vaccine.slot.notifier.data.model.response.SubscribeSlotsResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("calendarByDistrict")
    suspend fun getSlotsDistrictWise(
            @Query("district_id") district_id: Int,
            @Query("date") date: String
    ): Response<ApiResponse>

    @GET("calendarByPin")
    suspend fun getSlotsPinCodeWise(
            @Query("pincode") pincode: Int,
            @Query("date") date: String
    ): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun subscribeUserForSlot(
            @Body subscribeSlots: SubscribeSlots,
            @Url url: String
    ): Response<SubscribeSlotsResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun unsubscribeUserForSlot(
            @Body subscribeSlots: SubscribeSlots,
            @Url url: String
    ): Response<SubscribeSlotsResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun reportSlot(
            @Body reportAlert: ReportAlert,
            @Url url: String
    ): Response<ReportAlertResponse>

    @Headers("Content-Type: application/json")
    @GET
    suspend fun getInfo(
            @Url url: String
    ): Response<Info>
}