package com.example.aiub_buddy.api_service


import com.example.aiub_buddy.Faculty
import com.example.aiub_buddy.FacultyApi
import com.example.aiub_buddy.FacultyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {

    private val api: FacultyApi = Retrofit.Builder()
        .baseUrl("https://aiub-public-api.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FacultyApi::class.java)

    fun getAllFaculty(
        onSuccess: (List<Faculty>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getAllFaculty().enqueue(object : Callback<FacultyResponse> {
            override fun onResponse(
                call: Call<FacultyResponse>,
                response: Response<FacultyResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    onSuccess(response.body()?.data ?: emptyList())
                } else {
                    onError("API Error")
                }
            }

            override fun onFailure(call: Call<FacultyResponse>, t: Throwable) {
                onError(t.message ?: "Network Error")
            }
        })
    }
}