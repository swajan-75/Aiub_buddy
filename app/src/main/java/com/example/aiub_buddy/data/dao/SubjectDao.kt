package com.example.aiub_buddy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiub_buddy.data.entity.SubjectEntity

@Dao
interface SubjectDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(subjects : List<SubjectEntity>)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE name LIKE '%' || :query || '%'")
    fun searchSubjects(query: String): List<SubjectEntity>


}