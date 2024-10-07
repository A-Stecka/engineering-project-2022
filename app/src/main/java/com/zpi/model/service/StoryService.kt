package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.MessageDTO
import com.zpi.model.dto.PublishStoryDTO
import com.zpi.model.dto.RefDTO
import com.zpi.model.dto.StoryDTO
import com.zpi.model.entity.Prompt
import com.zpi.model.entity.Story
import com.zpi.model.repository.StoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StoryService {
    private var apiService: StoryRepository = StoryRepository.create(StoryRepository::class.java)

    fun getUserStories(userREF: Int, callback: (stories: List<Story>) -> Unit) {
        apiService.getUserStories(userREF)?.enqueue(object : Callback<List<StoryDTO>?> {

            override fun onResponse(call: Call<List<StoryDTO>?>, response: Response<List<StoryDTO>?>) {
                if (response.body() != null) {
                    val stories: MutableList<Story> = Converter.convertToStoryList(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
                }
            }
            override fun onFailure(call: Call<List<StoryDTO>?>, t: Throwable) {
                callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
            }

        })
    }

    fun getUserFavourites(userREF: Int, callback: (stories: List<Story>) -> Unit) {
        apiService.getUserFavourites(userREF)?.enqueue(object : Callback<List<StoryDTO>?> {

            override fun onResponse(call: Call<List<StoryDTO>?>, response: Response<List<StoryDTO>?>) {
                if (response.body() != null) {
                    val stories: MutableList<Story> = Converter.convertToStoryList(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
                }
            }
            override fun onFailure(call: Call<List<StoryDTO>?>, t: Throwable) {
                callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
            }

        })
    }

    fun searchStories(query: String, callback: (stories: List<Story>) -> Unit) {
        apiService.searchStories(query)?.enqueue(object : Callback<List<StoryDTO>?> {
            override fun onResponse(call: Call<List<StoryDTO>?>, response: Response<List<StoryDTO>?>) {
                if (response.body() != null) {
                    val stories: MutableList<Story> = Converter.convertToStoryList(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(listOf(Story(-1,"","","", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
                }
            }

            override fun onFailure(call: Call<List<StoryDTO>?>, t: Throwable) {
                callback.invoke(listOf(Story(-1,"","","", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
            }

        })
    }

    fun getStories(userREF: Int, callback: (stories: List<Story>) -> Unit) {
        apiService.getStories(userREF)?.enqueue(object : Callback<List<StoryDTO>?> {

            override fun onResponse(call: Call<List<StoryDTO>?>, response: Response<List<StoryDTO>?>) {
                if (response.body() != null) {
                    val stories: MutableList<Story> = Converter.convertToStoryList(response.body())
                    callback.invoke(stories)
                } else {
                    callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
                }
            }

            override fun onFailure(call: Call<List<StoryDTO>?>, t: Throwable) {
                callback.invoke(listOf(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList()))))
            }

        })
    }

    fun getGeneratedStory(storyREF: Int, callback: (stories: Story) -> Unit) {
        apiService.getGeneratedStory(storyREF)?.enqueue(object : Callback<StoryDTO?> {

            override fun onResponse(call: Call<StoryDTO?>, response: Response<StoryDTO?>) {
                if (response.body() != null) {
                    val story: Story = Converter.convertToStory(response.body()!!)
                    callback.invoke(story)
                } else {
                    callback.invoke(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList())))
                }
            }

            override fun onFailure(call: Call<StoryDTO?>, t: Throwable) {
                callback.invoke(Story(-1, "", "", "", LocalDateTime.now(), -1, Prompt(-1, "", emptyList())))
            }

        })
    }

    fun publishStory(story: Story, callback: (success: Boolean) -> Unit) {
        val storyDTO = Converter.convertToStoryDTO(story)

        apiService.publishStory(storyDTO)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.i("Publish story succeeded", "")
                if (response.body() != null) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                Log.i("Publish story failed", "")
                callback.invoke(false)
            }
        })
    }

    fun removeStory(storyREF: Int, callback: (success: Boolean) -> Unit) {
        apiService.removeStory(storyREF)?.enqueue(object : Callback<MessageDTO?> {
            override fun onResponse(call: Call<MessageDTO?>, response: Response<MessageDTO?>) {
                Log.i("Delete story succeeded", "")
                if (response.body() != null) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }

            override fun onFailure(call: Call<MessageDTO?>, t: Throwable) {
                Log.i("Delete story failed", "")
                callback.invoke(false)
            }
        })
    }

}