package com.vaccine.slot.notifier.ui.showSlots

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaccine.slot.notifier.data.Event
import com.vaccine.slot.notifier.data.model.Response
import com.vaccine.slot.notifier.retrofit.ApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val CO_WIN_API = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/"

class ShowSlotsViewModel : ViewModel() {
    private val _slotDetailsDistrict = MutableLiveData<List<Response>>()
    val slotDetailsDistrict: LiveData<List<Response>> get() = _slotDetailsDistrict

    private val _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> get() = _toast

    fun getSlotDetailsDistrictWise(code: Int) {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                    .addHeader("Authority", "cdn-api.co-vin.in")
                    .addHeader("Accept", "application/json")
                    .addHeader("Accept-Language", "hi_IN")
                    .addHeader("User-Agent", "PostmanRuntime/7.28.0")
                    .build()
            chain.proceed(request)
        }

        val retrofit = Retrofit.Builder().apply {
            baseUrl(CO_WIN_API)
            addConverterFactory(GsonConverterFactory.create())
            client(httpClient.build())
        }.build()

        val apiService = retrofit.create(ApiService::class.java)

        // GET CURRENT DATE FROM DEVICE
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        try {
            apiService?.getSlotsDistrictWise(code, currentDate)?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
//                    Log.d("Response", response.body()?.string() ?: "error")
                    parseResponse(response.body()?.string() ?: "empty")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Response", t.localizedMessage!!)
                }

            })
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            _toast.value = Event("Something went wrong. Please try again after sometime")
        }

    }

    private fun parseResponse(response: String) {
        try {
            if (response != "empty") {
//                Log.d("Response", response)
                val centerObject = JSONObject(response)
                val centerArray = centerObject.getJSONArray("centers")
                val slotAvailabilityList = ArrayList<Response>()
                for (i in 0 until centerArray.length()) {
                    val item = centerArray.getJSONObject(i)
                    val centerName = item.getString("name")
                    val districtName = item.getString("district_name")
                    val blockName = item.getString("block_name")
                    val pincode = item.getInt("pincode")
                    val feeType = item.getString("fee_type")
                    val address = item.getString("address")
                    val sessionArray = item.getJSONArray("sessions")
                    for (j in 0 until sessionArray.length()) {
                        val itemSession = sessionArray.getJSONObject(j)
                        if (itemSession.getInt("available_capacity") > 0) {
                            slotAvailabilityList.add(
                                    Response(
                                            center = centerName,
                                            districtName = districtName,
                                            blockName = blockName,
                                            pincode = pincode,
                                            fee = feeType,
                                            address = address,
                                            date = itemSession.getString("date"),
                                            availableCapacity = itemSession.getInt("available_capacity"),
                                            vaccine = itemSession.getString("vaccine"),
                                            slots = itemSession.getJSONArray("slots")
                                    )
                            )
                        }
                    }
                }
                _slotDetailsDistrict.value = slotAvailabilityList
            }
        } catch (e: JSONException) {
            Log.d("JSONException", e.localizedMessage!!)
            _slotDetailsDistrict.value = listOf()
        } catch (e1: Exception) {
            Log.d("ExceptionParse", e1.localizedMessage!!)
            _slotDetailsDistrict.value = listOf()
        }
    }
}