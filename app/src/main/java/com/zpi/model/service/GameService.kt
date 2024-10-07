package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.GameStoryDTO
import com.zpi.model.dto.MinigameAnswerDTO
import com.zpi.model.dto.RefDTO
import com.zpi.model.entity.GameStory
import com.zpi.model.entity.Prompt
import com.zpi.model.entity.Story
import com.zpi.model.repository.GameRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GameService {
    private var apiService: GameRepository = GameRepository.create(GameRepository::class.java)

    fun getGameRoundStories(genreREF: Int, userREF: Int, callback: (stories: List<GameStory>) -> Unit) {
        apiService.getRoundStories(genreREF, userREF)?.enqueue(object : Callback<List<GameStoryDTO>?> {
            override fun onResponse(call: Call<List<GameStoryDTO>?>, response: Response<List<GameStoryDTO>?>) {
                if (response.body() != null) {
                    val stories: MutableList<GameStory> = Converter.convertToGameStoryList(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(emptyList())
                }
            }

            override fun onFailure(call: Call<List<GameStoryDTO>?>, t: Throwable) {
                callback.invoke(emptyList())
            }

        })
    }

    fun addMinigameAnswer(correct: Double, userRef: Int, storyRef: Int, callback: (success: Boolean) -> Unit) {
        val minigameAnswerDTO = MinigameAnswerDTO(correct,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
            userRef, storyRef)
        apiService.addMinigameAnswer(minigameAnswerDTO)?.enqueue(object : Callback<RefDTO?> {
            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                if (response.body() != null)
                    callback.invoke(true)
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                callback.invoke(false)
            }
        })
    }

}