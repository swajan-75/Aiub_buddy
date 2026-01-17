package com.example.aiub_buddy.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiub_buddy.data.entity.StudentEntity

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM student LIMIT 1")
    fun getLoggedInStudent(): StudentEntity?

    @Query("DELETE FROM student")
    fun logout()
}
