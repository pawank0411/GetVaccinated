package com.vaccine.slot.notifier.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "response")
data class NotificationResponseRoom(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "content") val content: String?,
        @ColumnInfo(name = "title") val title: String?,
        @ColumnInfo(name = "time") val time: String?
)
