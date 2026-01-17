package com.example.aiub_buddy

import retrofit2.Call
import retrofit2.http.GET

data class NoticeApiResponse(
    val status: String,
    val data: List<NoticeRemoteModel>
)

data class NoticeRemoteModel(
    val date: String,
    val month: String,
    val year: String,
    val title: String,
    val desc: String,
    val link: String
)

interface NoticeApiService {
    @GET("notice/10")
    fun getNotices(): Call<NoticeApiResponse>
}
