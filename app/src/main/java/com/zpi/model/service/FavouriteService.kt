package com.zpi.model.service

import android.util.Log
import com.zpi.model.dto.MessageDTO
import com.zpi.model.dto.RefDTO
import com.zpi.model.repository.FavouriteRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouriteService {

    private var apiService: FavouriteRepository = FavouriteRepository.create(FavouriteRepository::class.java)

    fun getFavouriteRef(userREF: Int, storyREF: Int, callback: (ref: Int) -> Unit) {
        apiService.getFavouriteRef(userREF, storyREF)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.i("Get favourite ref succeeded", "")
                if (response.body() != null) {
                    response.body()!!.ref?.let { callback.invoke(it) }
                } else {
                    callback.invoke(-10)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                Log.i("Get favourite ref failed", "")
                callback.invoke(-10)
            }
        })
    }

    fun addFavourite(fkUser: Int, fkStory: Int, callback: (ref: Int) -> Unit) {
        apiService.addFavourite(fkUser, fkStory)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.i("Add to favourites succeeded", "")
                if (response.body() != null) {
                    response.body()!!.ref?.let { callback.invoke(it) }
                } else {
                    callback.invoke(-10)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                Log.i("Add to favourites failed", "")
                callback.invoke(-10)
            }
        })

    }

    fun removeFavourite(favREF: Int, callback: (ref: Int) -> Unit) {
        apiService.removeFavourite(favREF)?.enqueue(object : Callback<MessageDTO?> {
            override fun onResponse(call: Call<MessageDTO?>, response: Response<MessageDTO?>) {
                Log.i("Delete favourite succeeded", "")
                if (response.body() != null) {
                    callback.invoke(0)
                } else {
                    callback.invoke(-10)
                }
            }

            override fun onFailure(call: Call<MessageDTO?>, t: Throwable) {
                Log.i("Delete favourite failed", "")
                callback.invoke(-10)
            }
        })
    }
}