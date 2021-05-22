package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class DistrictResponse(
    @SerializedName("centers")
    val centers: List<Center>
)
