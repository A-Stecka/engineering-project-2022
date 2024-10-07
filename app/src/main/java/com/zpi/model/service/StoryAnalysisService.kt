package com.zpi.model.service

import com.zpi.model.Converter
import com.zpi.model.dto.StoryAnalysisDTO
import com.zpi.model.entity.StoryAnalysis
import com.zpi.model.repository.StoryAnalysisRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryAnalysisService {
    private var apiService: StoryAnalysisRepository = StoryAnalysisRepository.create(StoryAnalysisRepository::class.java)

    fun getStoryAnalysis(storyREF: Int, callback: (analysis: StoryAnalysis?) -> Unit) {
        apiService.getStoryAnalysis(storyREF)?.enqueue(object : Callback<StoryAnalysisDTO?> {

            override fun onResponse(call: Call<StoryAnalysisDTO?>, response: Response<StoryAnalysisDTO?>) {
                if (response.body() != null) {
                    val analysis: StoryAnalysis = Converter.convertToStoryAnalysis(response.body())
                    callback.invoke(analysis)
                }
            }

            override fun onFailure(call: Call<StoryAnalysisDTO?>, t: Throwable) {
                callback.invoke(null)
            }

        })
    }
}