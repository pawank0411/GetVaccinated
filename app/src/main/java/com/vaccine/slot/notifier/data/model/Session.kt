package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class Session(
        @SerializedName("session_id")
        val sessionId: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("available_capacity")
        val availableCapacity: Double?,
        @SerializedName("min_age_limit")
        val minAgeLimit: Int?,
        @SerializedName("vaccine")
        val vaccine: String?,
        @SerializedName("slots")
        val slots: List<String>?,
        @SerializedName("available_capacity_dose1")
        val availableCapacityDose1: Double?,
        @SerializedName("available_capacity_dose2")
        val availableCapacityDose2: Double?
)
