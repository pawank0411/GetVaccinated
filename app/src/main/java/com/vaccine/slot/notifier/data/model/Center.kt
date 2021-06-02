package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class Center(
        @SerializedName("center_id")
        val centerId: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("pincode")
        val pincode: Int?,
        @SerializedName("fee_type")
        val feeType: String?,
        @SerializedName("sessions")
        val sessions: List<Session>?,
        @SerializedName("vaccine_fees")
        val vaccineFee: List<VaccineFee>?
)
