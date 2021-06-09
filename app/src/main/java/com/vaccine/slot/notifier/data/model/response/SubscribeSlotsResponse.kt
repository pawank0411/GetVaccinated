package com.vaccine.slot.notifier.data.model.response

import com.google.gson.annotations.SerializedName

data class SubscribeSlotsResponse(
        @SerializedName("message")
        val message: String?,
        @SerializedName("success")
        val status: Boolean
)
