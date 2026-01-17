package com.example.aiub_buddy.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey
    val studentId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImg: String?
)
