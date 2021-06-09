package com.vaccine.slot.notifier.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_slots")
data class SubscribedSlotsRoom(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "clientID") val clientID: String?,
        @ColumnInfo(name = "age") val age: Int?,
        @ColumnInfo(name = "stateID") val stateID: String?,
        @ColumnInfo(name = "stateName") val stateName: String?,
        @ColumnInfo(name = "districtID") val districtID: Int?,
        @ColumnInfo(name = "districtName") val districtName: String?,
        @ColumnInfo(name = "vaccineID") val vaccineID: List<String>?,
        @ColumnInfo(name = "doseID") val doseID: List<String>?
)