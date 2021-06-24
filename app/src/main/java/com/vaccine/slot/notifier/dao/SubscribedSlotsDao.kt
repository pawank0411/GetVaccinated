package com.vaccine.slot.notifier.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaccine.slot.notifier.data.model.room.SubscribedSlotsRoom

@Dao
interface SubscribedSlotsDao {

    @Query("SELECT * FROM subscribed_slots")
    fun getAll(): LiveData<List<SubscribedSlotsRoom>>?

    @Query("DELETE FROM subscribed_slots WHERE id = :id")
    suspend fun deleteDataById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscribedSlotsRoom: SubscribedSlotsRoom)
}