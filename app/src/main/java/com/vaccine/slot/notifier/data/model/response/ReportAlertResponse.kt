package com.vaccine.slot.notifier.data.model.response

import com.google.gson.annotations.SerializedName

data class ReportAlertResponse(
        @SerializedName("success")
        val status: Boolean?
)
