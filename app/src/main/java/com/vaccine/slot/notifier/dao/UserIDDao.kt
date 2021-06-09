package com.vaccine.slot.notifier.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaccine.slot.notifier.data.model.room.UserID

@Dao
interface UserIDDao {

    @Query("SELECT * FROM user_Id")
    fun getPlayerID(): LiveData<UserID?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userID: UserID)
}