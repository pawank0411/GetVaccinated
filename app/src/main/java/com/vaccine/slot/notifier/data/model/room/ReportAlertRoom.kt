package com.vaccine.slot.notifier.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report")
data class ReportAlertRoom(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "age") val age: Int?,
        @ColumnInfo(name = "stateID") val stateID: String?,
        @ColumnInfo(name = "stateName") val stateName: String?,
        @ColumnInfo(name = "districtID") val districtID: Int?,
        @ColumnInfo(name = "districtName") val districtName: String?,
)
