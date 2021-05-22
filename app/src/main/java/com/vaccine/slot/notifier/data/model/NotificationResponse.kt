package com.vaccine.slot.notifier.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "response")
data class NotificationResponse(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "content") val content: String?,
        @ColumnInfo(name = "title") val title: String?
)
