package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("available_capacity")
    val availableCapacityDose: Double?,
    @SerializedName("min_age_limit")
    val minAgeLimit: Int?,
    @SerializedName("max_age_limit")
    val maxAgeLimit: Int?,
    @SerializedName("allow_all_age")
    val allowAllAge: Boolean?,
    @SerializedName("vaccine")
    val vaccine: String?,
    @SerializedName("available_capacity_dose1")
    val availableCapacityDose1: Double?,
    @SerializedName("available_capacity_dose2")
    val availableCapacityDose2: Double?
)
