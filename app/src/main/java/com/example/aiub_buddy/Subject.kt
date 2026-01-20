package com.example.aiub_buddy

import java.time.LocalTime

data class Subject(
    val subject_id : String,
    val subject_name : String,
    val time : subject_time,
    val room : String,
    val routine_id : String,
    val day : String
)



data class subject_time(
    val starting_time : String,
    val ending_tine : String
)
