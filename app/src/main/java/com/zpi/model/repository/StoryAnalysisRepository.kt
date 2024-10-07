package com.zpi.model.repository

import com.zpi.model.dto.StoryAnalysisDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface StoryAnalysisRepository {

    @GET("/getStoryAnalysis/{storyREF}")
    fun getStoryAnalysis(@Path("storyREF") storyREF: Int?): Call<StoryAnalysisDTO>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<StoryAnalysisRepository>): StoryAnalysisRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }

}