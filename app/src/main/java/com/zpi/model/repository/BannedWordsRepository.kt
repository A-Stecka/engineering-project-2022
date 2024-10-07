package com.zpi.model.repository

import com.zpi.model.dto.BannedWordDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BannedWordsRepository {

    @GET("/getBannedWords")
    fun getBannedWords(): Call<List<BannedWordDTO>?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<BannedWordsRepository>): BannedWordsRepository {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }
}