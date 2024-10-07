package com.zpi.model.repository

import com.zpi.model.dto.PromptDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface PromptRepository {

    @GET("/generateChallenge/{words}")
    fun generateChallenge(@Path("words") noOfWords: Int?): Call<PromptDTO?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<PromptRepository>): PromptRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }

}