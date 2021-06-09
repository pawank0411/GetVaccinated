package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class ReportAlert(
        @SerializedName("age")
        val age: Int?,
        @SerializedName("stateID")
        val stateID: String?,
        @SerializedName("districtID")
        val districtID: Int?
)