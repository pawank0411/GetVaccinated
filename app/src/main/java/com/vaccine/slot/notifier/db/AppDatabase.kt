package com.vaccine.slot.notifier.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vaccine.slot.notifier.dao.NotificationDao
import com.vaccine.slot.notifier.dao.ReportDao
import com.vaccine.slot.notifier.dao.SubscribedSlotsDao
import com.vaccine.slot.notifier.dao.UserIDDao
import com.vaccine.slot.notifier.data.model.room.NotificationResponseRoom
import com.vaccine.slot.notifier.data.model.room.ReportAlertRoom
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom
import com.vaccine.slot.notifier.data.model.room.UserID

@Database(entities = [NotificationResponseRoom::class, SubscribedSlotsRoom::class, ReportAlertRoom::class, UserID::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun subscribedSlotsDao(): SubscribedSlotsDao
    abstract fun reportDao(): ReportDao
    abstract fun userIDDao(): UserIDDao
}