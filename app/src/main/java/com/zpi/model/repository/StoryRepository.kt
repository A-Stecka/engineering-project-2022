package com.zpi.model.repository

import com.zpi.model.dto.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoryRepository {

    @GET("/getUserStories/{userREF}")
    fun getUserStories(@Path("userREF") userREF: Int?): Call<List<StoryDTO>?>?

    @GET("/getUserFavourites/{userREF}")
    fun getUserFavourites(@Path("userREF") userREF: Int?): Call<List<StoryDTO>?>?

    @GET("/getStories/{userREF}")
    fun getStories(@Path("userREF") userREF: Int?): Call<List<StoryDTO>?>?

    @GET("/getGeneratedStory/{storyREF}")
    fun getGeneratedStory(@Path("storyREF") storyREF: Int?): Call<StoryDTO>?

    @GET("/search/{query}")
    fun searchStories(@Path("query") query: String): Call<List<StoryDTO>?>?

    @GET("/removeStory/{storyRef}")
    fun removeStory(@Path("storyRef") storyREF: Int?): Call<MessageDTO>?

    @POST("/publishStory")
    fun publishStory(@Body story: PublishStoryDTO): Call<RefDTO>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<StoryRepository>): StoryRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }

}