package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.PromptDTO
import com.zpi.model.dto.StatisticsDTO
import com.zpi.model.dto.StoriesPerGenreDTO
import com.zpi.model.dto.StoryDTO
import com.zpi.model.entity.Prompt
import com.zpi.model.entity.Statistics
import com.zpi.model.entity.StoriesPerGenre
import com.zpi.model.entity.Story
import com.zpi.model.repository.StatisticsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StatisticsService {
    private var apiService: StatisticsRepository = StatisticsRepository.create(StatisticsRepository::class.java)

    fun getUserStatistics(userRef: Int, callback: (stats: Statistics) -> Unit) {
        apiService.getUserStatistics(userRef)?.enqueue(object : Callback<StatisticsDTO?> {

            override fun onResponse(call: Call<StatisticsDTO?>, response: Response<StatisticsDTO?>) {
                Log.i("Get user statistics called", "")
                if (response.body() != null) {
                    Log.i("Get user statistics returned", response.body().toString())
                    callback.invoke(Converter.convertToStatistics(response.body()))
                } else {
                    callback.invoke(Statistics(-10, -1, -1, -1,
                        -1, -1, -1, -1.0, -1,
                        -1))
                }
            }

            override fun onFailure(call: Call<StatisticsDTO?>, t: Throwable) {
                Log.e("Get user statistics failed", "")
                callback.invoke(Statistics(-10, -1, -1, -1,
                    -1, -1, -1, -1.0, -1,
                    -1))
            }
        })
    }

    fun getUserStatisticsPerGenre(userRef: Int, callback: (stats: List<StoriesPerGenre>) -> Unit) {
        apiService.getUserStatisticsPerGenre(userRef)?.enqueue(object : Callback<List<StoriesPerGenreDTO>?> {

            override fun onResponse(call: Call<List<StoriesPerGenreDTO>?>, response: Response<List<StoriesPerGenreDTO>?>) {
                Log.i("Get user statistics per genre called", "")
                if (response.body() != null) {
                    Log.i("Get user statistics per genre returned", response.body().toString())
                    val stories: MutableList<StoriesPerGenre> = Converter.convertToStoriesPerGenre(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(listOf(StoriesPerGenre("",  -1)))
                }
            }

            override fun onFailure(call: Call<List<StoriesPerGenreDTO>?>, t: Throwable) {
                Log.e("Get user statistics per genre failed", "")
                callback.invoke(listOf(StoriesPerGenre("",  -1)))
            }
        })
    }
}