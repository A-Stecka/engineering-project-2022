package com.zpi.model.repository

import com.zpi.model.dto.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UserRepository {

    @GET("/getUserPassword/{login}")
    fun getUserPassword(@Path("login") login: String?): Call<GetUserPasswordDTO?>?

    @GET("/getUserEmail/{login}")
    fun getUserEmail(@Path("login") login: String?): Call<GetUserEmailDTO?>?

    @GET("/getUserLogins/{email}")
    fun getUserLogins(@Path("email") email: String?): Call<GetUserLoginsDTO?>?

    @POST("/registerUser")
    fun registerUser(@Body user: UserDTO): Call<RefDTO>?

    @GET("/getUser/{ref_user}")
    fun getUser(@Path("ref_user") refUser: Int?): Call<UserDTO?>?

    @GET("/getUserRef/{login}")
    fun getUserRef(@Path("login") login: String?): Call<RefDTO?>?

    @PUT("/changePassword")
    fun changePassword(@Body user: UserDTO): Call<UserDTO?>?

    @PUT("/changeUsername")
    fun changeUsername(@Body user: UserDTO): Call<UserDTO?>?

    @PUT("/changeProfilePicture")
    fun changeProfilePicture(@Body user: UserDTO): Call<UserDTO?>?

    @PUT("/changeEmail")
    fun changeEmail(@Body user: UserDTO): Call<UserDTO?>?

    @GET("/getLeaderboard")
    fun getLeaderboard(): Call<List<LeaderboardItemDTO>?>?

    companion object {
        var BASE_URL = "https://zpi-aaab.herokuapp.com/"

        fun create(apiInterface: Class<UserRepository>): UserRepository {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(apiInterface)
        }
    }
}