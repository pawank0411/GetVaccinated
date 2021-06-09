package com.vaccine.slot.notifier.data.model.response

import com.google.gson.annotations.SerializedName
import com.vaccine.slot.notifier.data.model.Center

data class ApiResponse(
        @SerializedName("centers")
        val centers: List<Center>
)
