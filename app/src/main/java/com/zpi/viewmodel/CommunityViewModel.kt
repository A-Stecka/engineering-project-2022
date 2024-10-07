package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.Genre
import com.zpi.model.entity.Story
import com.zpi.model.service.GenreService
import com.zpi.model.service.StoryService

class CommunityViewModel : ViewModel() {

    private val storyService: StoryService = StoryService()
    private val genreService: GenreService = GenreService()
    private val pickedGenres: MutableList<Genre> = mutableListOf()

    var userREF: Int? = null

    private val _storyList = MutableLiveData<List<Story>>().apply {
        value = emptyList()
    }

    private val _showingStoryList = MutableLiveData<List<Story>>().apply {
        value = emptyList()
    }

    private val _genresList = MutableLiveData<List<Genre>>().apply {
        value = emptyList()
    }

    val stories: MutableLiveData<List<Story>> = _showingStoryList

    fun init() {
        genreService.getGenres { genres ->
            _genresList.value = genres
            pickedGenres.addAll(genres)
        }
    }

    private fun setUserStories() {
        storyService.getStories(userREF!!, ::getUserStories)
    }

    private fun getUserStories(stories: List<Story>) {
        _storyList.value = stories
        _showingStoryList.value = stories
    }

    fun setUserRef(userREF: Int?) {
        Log.i("CommunityViewModel got user", userREF.toString())
        this.userREF = userREF
        setUserStories()
    }

    fun changeGenre(genre: Genre) {
        if (pickedGenres.any { pickedGenre -> pickedGenre.value == genre.value }) {
            pickedGenres.remove(genre)
            Log.i("PICKED", "Removed " + genre.value)
        } else {
            pickedGenres.add(genre)
            Log.i("PICKED", "Added " + genre.value)
        }
        pickStoriesForGenres()
    }

    fun getGenres(): MutableLiveData<List<Genre>> {
        return _genresList
    }

    private fun pickStoriesForGenres() {
        _showingStoryList.value = _storyList.value!!.filter { story -> pickedGenres.any { genre -> genre.value == story.prompt.genre } }
    }
}