package com.zpi.model.repository

import com.zpi.model.dto.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentRepository {

    @GET("/getStoryComments/{storyREF}")
    fun getStoryComments(@Path("storyREF")storyREF: Int?): Call<List<CommentDTO>?>?

    @POST("/publishComment")
    fun publishComment(@Body comment: PublishCommentDTO): Call<RefDTO>?

    @GET("/removeComment/{commentREF}")
    fun removeComment(@Path("commentREF")commentREF: Int?): Call<MessageDTO>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<CommentRepository>): CommentRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }

}
