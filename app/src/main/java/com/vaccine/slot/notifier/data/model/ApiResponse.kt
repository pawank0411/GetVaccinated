package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
        @SerializedName("centers")
        val centers: List<Center>
)
