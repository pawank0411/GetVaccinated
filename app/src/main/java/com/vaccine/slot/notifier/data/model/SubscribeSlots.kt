package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class SubscribeSlots(
        @SerializedName("client")
        val clientId: String?,
        @SerializedName("age")
        val preferredAge: Int?,
        @SerializedName("stateID")
        val preferredStateID: String?,
        @SerializedName("districtID")
        val preferredDistrictID: Int?,
        @SerializedName("vaccineID")
        val preferredVaccineID: List<String>?,
        @SerializedName("doseID")
        val preferredDoseID: List<String>?
)
