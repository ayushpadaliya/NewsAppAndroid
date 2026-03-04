package com.example.newsapp.network

import com.example.newsapp.dashboard.data.model.GetNewsDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("everything")
    suspend fun getNewsData(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int,
    ): Response<GetNewsDetailsResponse>
}
