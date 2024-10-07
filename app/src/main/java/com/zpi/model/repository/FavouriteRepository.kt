package com.zpi.model.repository

import com.zpi.model.dto.MessageDTO
import com.zpi.model.dto.RefDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FavouriteRepository {

    @GET("/getFavouriteRef/{userREF}/{storyREF}")
    fun getFavouriteRef(@Path("userREF") userREF: Int?, @Path("storyREF") storyREF: Int?): Call<RefDTO>?

    @GET("/addFavourite/{userREF}/{storyREF}")
    fun addFavourite(@Path("userREF") userREF: Int?, @Path("storyREF") storyREF: Int?): Call<RefDTO?>?

    @GET("/removeFavourite/{favREF}")
    fun removeFavourite(@Path("favREF") favREF: Int?): Call<MessageDTO?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<FavouriteRepository>): FavouriteRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }
}
