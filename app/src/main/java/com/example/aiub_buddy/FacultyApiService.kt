package com.example.aiub_buddy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

data class Faculty(
    val name: String,
    val email: String,
    val faculty: String,
    val designation: String,
    val position: String,
    val department: String,
    val profile_photo: String,
    val profile_link: String,
    val room_number: String,
    val building_number: String,
    val academic_interests: List<String>,
    val research_interests: List<String>
)

data class FacultyResponse(
    val status: String,
    val total: Int,
    val data: List<Faculty>
)

interface FacultyApi {
    @GET("faculty/all")
    fun getAllFaculty(): Call<FacultyResponse>

}
