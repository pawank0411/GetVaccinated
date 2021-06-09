package com.vaccine.slot.notifier.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaccine.slot.notifier.data.model.room.ReportAlertRoom

@Dao
interface ReportDao {

    @Query("SELECT * FROM report WHERE id = :id")
    fun getIdWiseData(id: Int): LiveData<ReportAlertRoom?>

    @Query("DELETE FROM report WHERE id = :id")
    suspend fun deleteDataById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reportAlertRoom: ReportAlertRoom)
}