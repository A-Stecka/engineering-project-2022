package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.GameStory
import com.zpi.model.entity.Genre
import com.zpi.model.entity.LeaderboardItem
import com.zpi.model.service.GameService
import com.zpi.model.service.GenreService
import com.zpi.model.service.UserService

class GameViewModel : ViewModel() {

    private val gameService: GameService = GameService()
    private val genreService: GenreService = GenreService()
    private val userService: UserService = UserService()

    private val _leadersList = MutableLiveData<List<LeaderboardItem>>().apply {
        value = listOf()
    }
    val leaders: MutableLiveData<List<LeaderboardItem>> = _leadersList

    private val _genresList = MutableLiveData<List<Genre>>().apply {
        value = listOf()
    }
    val genres: MutableLiveData<List<Genre>> = _genresList

    private val _aiStory = MutableLiveData<GameStory>()
    val aiStory = _aiStory
    private val _userStory = MutableLiveData<GameStory>()
    val userStory = _userStory

    var userREF: Int? = null

    fun setUserRef(userREF: Int?) {
        Log.i("GameViewModel got user", userREF.toString())
        this.userREF = userREF
    }

    fun getLeaderboard() {
        userService.getLeaderboard { leaders -> _leadersList.value = leaders }
    }

    fun getGenres() {
        genreService.getGenres { genres -> _genresList.value = genres }
    }

    fun prepareStoryPair(genreREF: Int, callback: (success: Boolean) -> Unit) {
        gameService.getGameRoundStories(genreREF, userREF!!) { stories ->
            if (stories.isNotEmpty()) {
                _aiStory.value = stories[1]
                _userStory.value = stories[0]
                callback.invoke(true)
            } else {
                Log.i("GAME", "empty")
                callback.invoke(false)
            }
        }
    }

    fun addMinigameResult(correct: Double, userRef: Int, storyRef: Int, callback: (success: Boolean) -> Unit) {
        gameService.addMinigameAnswer(correct, userRef, storyRef, callback)
    }

}