package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class Center(
        @SerializedName("center_id")
        val centerId: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("state_name")
        val stateName: String?,
        @SerializedName("district_name")
        val districtName: String?,
        @SerializedName("block_name")
        val blockName: String?,
        @SerializedName("pincode")
        val pincode: Int?,
        @SerializedName("lat")
        val lat: Int?,
        @SerializedName("long")
        val long: Int?,
        @SerializedName("from")
        val from: String?,
        @SerializedName("to")
        val to: String?,
        @SerializedName("fee_type")
        val feeType: String?,
        @SerializedName("sessions")
        var sessions: List<Session>?
)
