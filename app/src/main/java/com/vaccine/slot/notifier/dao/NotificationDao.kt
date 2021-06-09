package com.vaccine.slot.notifier.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaccine.slot.notifier.data.model.room.NotificationResponseRoom

@Dao
interface NotificationDao {

    @Query("SELECT * from response")
    fun getAll(): LiveData<List<NotificationResponseRoom>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationResponseRoom: NotificationResponseRoom)
}