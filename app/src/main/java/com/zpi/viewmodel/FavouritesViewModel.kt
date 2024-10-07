package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.Story
import com.zpi.model.service.StoryService

class FavouritesViewModel : ViewModel() {

    private val storyService: StoryService = StoryService()

    private val _storyList = MutableLiveData<List<Story>>().apply {
        value = emptyList()
    }

    var userREF: Int? = null

    val favourites: MutableLiveData<List<Story>> = _storyList

    fun setUserRef(userRef: Int?) {
        Log.i("FavouritesViewModel got user", userRef.toString())
        this.userREF = userRef
        setUserFavourites()
    }

    private fun setUserFavourites() {
        storyService.getUserFavourites(userREF!!, ::getUserFavourites)
    }

    private fun getUserFavourites(stories: List<Story>) {
        _storyList.value = stories
    }

}
