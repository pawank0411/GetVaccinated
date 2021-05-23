package com.vaccine.slot.notifier.data.model

import com.google.gson.annotations.SerializedName

data class StateList(
        @SerializedName("states")
        val states: List<State>
)
