package com.zpi.model.repository

import com.zpi.model.dto.GameStoryDTO
import com.zpi.model.dto.MinigameAnswerDTO
import com.zpi.model.dto.PublishCommentDTO
import com.zpi.model.dto.RefDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GameRepository {

    @GET("/getMinigameItem/{genreREF}/{userREF}")
    fun getRoundStories(@Path("genreREF") genreREF: Int?, @Path("userREF") userREF: Int?): Call<List<GameStoryDTO>?>?

    @POST("/addMinigameAnswer")
    fun addMinigameAnswer(@Body answer: MinigameAnswerDTO): Call<RefDTO>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<GameRepository>): GameRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }

}