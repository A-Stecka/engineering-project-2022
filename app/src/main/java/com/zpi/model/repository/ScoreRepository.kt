package com.zpi.model.repository

import com.zpi.model.dto.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ScoreRepository {

    @POST("/addScore")
    fun addScore(@Body score: ScoreDTO): Call<ScoreValueDTO?>?

    @GET("/getAverageScore/{story_ref}")
    fun getAverageScore(@Path("story_ref") storyREF: Int): Call<AverageDTO?>?

    @GET("/getScore/{story_ref}/{user_ref}")
    fun getScore(@Path("story_ref") storyREF: Int, @Path("user_ref") userREF: Int): Call<ScoreValueDTO?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<ScoreRepository>): ScoreRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }
}
