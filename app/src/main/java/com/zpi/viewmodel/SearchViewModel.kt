package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.Story
import com.zpi.model.service.StoryService

class SearchViewModel : ViewModel() {

    private val storyService: StoryService = StoryService()

    private val _storyList = MutableLiveData<List<Story>>().apply {
        value = emptyList()
    }

    private val _query = MutableLiveData<String>().apply {
        value = ""
    }

    val stories: MutableLiveData<List<Story>> = _storyList
    val query: MutableLiveData<String> = _query

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun searchForStories(query: String) {
        storyService.searchStories(query) { stories ->
            _storyList.value = stories
            Log.i("SEARCH", "stories received " + stories.size)
        }
    }

}