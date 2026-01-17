package com.example.aiub_buddy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aiub_buddy.data.entity.RoutineEntity

@Dao
interface RoutineDao{
    @Query("SELECT * FROM routine WHERE day = :day")
    fun getRoutineByDay(day: String): List<RoutineEntity>

    @Query("SELECT * FROM routine")
    fun getAllRoutine(): List<RoutineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoutine(routine: RoutineEntity)

    @Delete
    fun deleteRoutine(routine: RoutineEntity)

    @Update
    fun updateRoutine(routine: RoutineEntity)

    @Query("""
DELETE FROM routine 
WHERE subject = :subject 
AND day = :day 
AND time = :time 
AND room = :room
""")
    fun deleteByDetails(
        subject: String,
        day: String,
        time: String,
        room: String
    )

    @Query("DELETE FROM routine")
    fun deleteAll()







}