package com.example.aiub_buddy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiub_buddy.data.entity.FacultyEntity

@Dao
interface FacultyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(facultyList: List<FacultyEntity>)

    @Query("SELECT * FROM faculty")
    fun getAllFaculty(): List<FacultyEntity>
}
