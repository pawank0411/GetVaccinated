package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class VaccineFee(
        @SerializedName("vaccine")
        val vaccineName: String?,
        @SerializedName("fee")
        val fee: String?
)