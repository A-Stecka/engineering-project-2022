package com.zpi.model.repository

import com.zpi.model.dto.StatisticsDTO
import com.zpi.model.dto.StoriesPerGenreDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface StatisticsRepository {

    @GET("/getUserStatistics/{userREF}")
    fun getUserStatistics(@Path("userREF") userREF: Int?): Call<StatisticsDTO?>?

    @GET("/getUserStatisticsPerGenre/{userREF}")
    fun getUserStatisticsPerGenre(@Path("userREF") userREF: Int?): Call<List<StoriesPerGenreDTO>?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<StatisticsRepository>): StatisticsRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }
}