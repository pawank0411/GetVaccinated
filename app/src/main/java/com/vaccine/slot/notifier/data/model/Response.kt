package com.vaccine.slot.notifier.data.model

import org.json.JSONArray

data class Response(
        val center: String,
        val availableCapacity: Int,
        val vaccine: String,
        val fee: String,
        val date: String,
        val slots: JSONArray,
        val address: String,
        val blockName: String,
        val districtName: String,
        val pincode: Int
)
