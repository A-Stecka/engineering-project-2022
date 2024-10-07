package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.Story
import com.zpi.model.service.StoryService

class HomeViewModel : ViewModel() {

    private val storyService: StoryService = StoryService()

    private val _storyList = MutableLiveData<List<Story>>().apply {
        value = emptyList()
    }

    private var userREF: Int? = null

    val stories: MutableLiveData<List<Story>> = _storyList

    private fun setUserStories() {
        storyService.getUserStories(userREF!!, ::getUserStories)
    }

    private fun getUserStories(stories: List<Story>) {
        _storyList.value = stories
    }

    fun setUserRef(userREF: Int?) {
        Log.i("HomeViewModel got user", userREF.toString())
        this.userREF = userREF
        setUserStories()
    }
}