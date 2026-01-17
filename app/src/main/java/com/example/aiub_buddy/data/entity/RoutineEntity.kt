package com.example.aiub_buddy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "routine")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject_id: String,
    val subject: String,
    val day: String,
    val time: String,
    val room: String

)