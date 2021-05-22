package com.vaccine.slot.notifier.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vaccine.slot.notifier.dao.NotificationDao
import com.vaccine.slot.notifier.data.model.NotificationResponse

@Database(entities = [NotificationResponse::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}