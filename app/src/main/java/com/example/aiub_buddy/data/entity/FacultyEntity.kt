package com.example.aiub_buddy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faculty")
data class FacultyEntity(
    @PrimaryKey val email: String,
    val name: String,
    val faculty: String,
    val designation: String,
    val position: String,
    val department: String,
    val profile_photo: String,
    val profile_link: String,
    val room_number: String,
    val building_number: String,
    val academic_interests: String,
    val research_interests: String
)
