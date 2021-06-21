package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class Info(
        @SerializedName("download_link")
        val downloadLink: String,
        @SerializedName("images")
        val imagesLink: List<String>?,
        @SerializedName("version")
        val version: String
)