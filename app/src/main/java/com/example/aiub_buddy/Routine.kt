package com.example.aiub_buddy

data class Routine(
    val id : Number,
    val subject_id : String,
    val courseName: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val roomNumber: String
)
