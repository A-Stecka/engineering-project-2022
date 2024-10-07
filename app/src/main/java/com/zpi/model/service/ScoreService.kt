package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.*
import com.zpi.model.entity.Score
import com.zpi.model.repository.ScoreRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoreService {
    private var apiService: ScoreRepository =
        ScoreRepository.create(ScoreRepository::class.java)

    fun addScore(score: Score, callback: (scoreRef: Int, value: Float) -> Unit) {
        val scoreDTO = Converter.convertToScoreDTO(score)
        apiService.addScore(scoreDTO)?.enqueue(object : Callback<ScoreValueDTO?> {

            override fun onResponse(call: Call<ScoreValueDTO?>, response: Response<ScoreValueDTO?>) {
                Log.i("Add score succeeded", "")
                if (response.body() != null) {
                    callback.invoke(response.body()!!.ref!!, response.body()!!.value!!)
                } else {
                    callback.invoke( -10, -10f)
                }
            }

            override fun onFailure(call: Call<ScoreValueDTO?>, t: Throwable) {
                Log.i("Add score failed", "")
                callback.invoke( -10, -10f)
            }
        })

    }

    fun getAverageScore(storyREF: Int, callback: (score: Float) -> Unit) {
        apiService.getAverageScore(storyREF)?.enqueue(object : Callback<AverageDTO?> {

            override fun onResponse(call: Call<AverageDTO?>, response: Response<AverageDTO?>) {
                Log.i("Get average score succeeded", "")
                if (response.body() != null)
                    callback.invoke(response.body()!!.averageScore!!)
                else
                    callback.invoke(-100.0f)
            }

            override fun onFailure(call: Call<AverageDTO?>, t: Throwable) {
                Log.i("Get score failed", "")
                callback.invoke(-100.0f)
            }
        })
    }

    fun getScoreValue(storyREF: Int, userREF: Int, callback: (scoreRef: Int, value: Float) -> Unit) {
        apiService.getScore(storyREF, userREF)?.enqueue(object : Callback<ScoreValueDTO?> {

            override fun onResponse(call: Call<ScoreValueDTO?>, response: Response<ScoreValueDTO?>) {
                Log.i("Get score ref succeeded", "")
                if (response.body() != null) {
                    callback.invoke(response.body()!!.ref!!, response.body()!!.value!!)
                } else {
                    callback.invoke(-100, -100f)
                }
            }

            override fun onFailure(call: Call<ScoreValueDTO?>, t: Throwable) {
                Log.i("Get score ref failed", "")
                callback.invoke(-100, -100f)
            }
        })
    }
}
