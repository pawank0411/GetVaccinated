package com.vaccine.slot.notifier.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_Id")
data class UserID(
        @PrimaryKey @ColumnInfo(name = "id") val id: Int = 0,
        @ColumnInfo(name = "player_id") val playerID: String?
)
