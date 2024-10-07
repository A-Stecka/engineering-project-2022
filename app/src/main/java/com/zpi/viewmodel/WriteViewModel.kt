package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.BannedWord
import com.zpi.model.entity.Prompt
import com.zpi.model.entity.Story
import com.zpi.model.service.BannedWordsService
import com.zpi.model.service.PromptService
import com.zpi.model.service.StoryService
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class WriteViewModel : ViewModel() {
    private val promptService: PromptService = PromptService()
    private val storyService: StoryService = StoryService()
    private val bannedWordsService: BannedWordsService = BannedWordsService()
    private var userRef: Int? = null

    private var _prompt: MutableLiveData<Prompt> = MutableLiveData<Prompt>().apply {
        value = Prompt(-10, "", listOf(""))
    }

    private var _bannedWords: MutableLiveData<List<BannedWord>> = MutableLiveData<List<BannedWord>>().apply {
        value = emptyList()
    }

    val prompt: MutableLiveData<Prompt> = _prompt
    val bannedWords: MutableLiveData<List<BannedWord>> = _bannedWords

    fun setUserRef(userRef: Int?) {
        Log.i("WriteViewModel got user", userRef.toString())
        this.userRef = userRef
    }

    fun generateChallenge(noOfWords: Int) {
        promptService.generateChallenge(noOfWords, ::setPrompt)
    }

    fun publishStory(title: String, content: String, callback: (success: Boolean) -> Unit) {
        val story = Story(-1, title, "", content, LocalDateTime.now(), userRef!!, _prompt.value!!)
        storyService.publishStory(story, callback)
    }

    fun getBannedWords() {
        bannedWordsService.getBannedWords(::setBannedWords)
    }

    private fun setPrompt(prompt: Prompt) {
        _prompt.value = prompt
    }

    private fun setBannedWords(bannedWords: MutableList<BannedWord>) {
        _bannedWords.value = bannedWords
    }

}