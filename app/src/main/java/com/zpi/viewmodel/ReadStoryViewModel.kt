package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.*
import com.zpi.model.service.*
import java.time.LocalDateTime

class ReadStoryViewModel: ViewModel() {

    private val bannedWordsService: BannedWordsService = BannedWordsService()
    private val storyService: StoryService = StoryService()
    private val storyAnalysisService: StoryAnalysisService = StoryAnalysisService()
    private val commentService: CommentService = CommentService()
    private val favouriteService: FavouriteService = FavouriteService()
    private val scoreService: ScoreService = ScoreService()

    var storyREF: Int? = null
    var userREF: Int? = null

    private var _bannedWords: MutableLiveData<List<BannedWord>> = MutableLiveData<List<BannedWord>>().apply {
        value = emptyList()
    }

    val bannedWords: MutableLiveData<List<BannedWord>> = _bannedWords

    private var _storyAnalysis: MutableLiveData<StoryAnalysis?> = MutableLiveData<StoryAnalysis?>().apply {
        value = null
    }

    val storyAnalysis: MutableLiveData<StoryAnalysis?> = _storyAnalysis

    private var _generatedStory: MutableLiveData<Story?> = MutableLiveData<Story?>().apply {
        value = null
    }

    val generatedStory: MutableLiveData<Story?> = _generatedStory

    private val _favouriteRef = MutableLiveData<Int>().apply {
        value = -1
    }

    val favouriteREF: MutableLiveData<Int> = _favouriteRef

    private val _scoreRef = MutableLiveData<Int>().apply {
        value = -1
    }

    val scoreRef: MutableLiveData<Int> = _scoreRef

    private val _scoreValue = MutableLiveData<Float>().apply {
        value = -1.0f
    }

    val scoreValue: MutableLiveData<Float> = _scoreValue

    private val _avgScore = MutableLiveData<Float>().apply {
        value = -10.0f
    }

    val avgScore: MutableLiveData<Float> = _avgScore

    private val _commentList = MutableLiveData<List<Comment>>().apply {
        value = emptyList()
    }

    val comments: MutableLiveData<List<Comment>> = _commentList

    fun setStoryREF(storyREF: Int) {
        this.storyREF = storyREF
        this.storyAnalysisService.getStoryAnalysis(storyREF) { analysis -> storyAnalysis.value = analysis }
    }

    fun setUserRef(userREF: Int?) {
        Log.i("ReadOtherStoryViewModel got user", userREF.toString())
        this.userREF = userREF
    }

    fun getComments() {
        commentService.getStoryComments(storyREF!!, ::getComments)
    }

    private fun getComments(comments: List<Comment>) {
        _commentList.value = comments
    }

    fun getGeneratedStory() {
        storyService.getGeneratedStory(storyREF!!, ::getGeneratedStory)
    }

    private fun getGeneratedStory(story: Story) {
        _generatedStory.value = story
    }

    fun removeStory(storyRef: Int, callback: (success: Boolean) -> Unit) {
        storyService.removeStory(storyRef, callback)
    }

    fun publishComment(content: String, callback: (success: Boolean, mode: Boolean) -> Unit) {
        val comment = Comment(-1, content, LocalDateTime.now(), "", 0, userREF!!, storyREF!!)
        commentService.publishComment(comment, callback)
    }

    fun removeComment(commentRef: Int, callback: (success: Boolean, mode: Boolean) -> Unit) {
        commentService.removeComment(commentRef, callback)
    }

    fun addFavourite(userRef: Int, storyRef: Int) {
        favouriteService.addFavourite(userRef, storyRef, ::setFavRef)
    }

    fun removeFavourite(favRef: Int) {
        favouriteService.removeFavourite(favRef, ::setFavRef)
    }

    fun getScoreValue(storyRef: Int, userRef: Int) {
        scoreService.getScoreValue(storyRef, userRef, ::setScore)
    }

    fun addScore(storyRef: Int, userRef: Int, value: Float) {
        val score = Score(-1, value, LocalDateTime.now(), storyRef, userRef)
        scoreService.addScore(score, ::setScore)
    }

    fun getAverageScore(storyRef: Int) {
        scoreService.getAverageScore(storyRef, ::setAvgScore)
    }

    fun getBannedWords() {
        bannedWordsService.getBannedWords(::setBannedWords)
    }

    fun getFavRef(userREF: Int) {
        favouriteService.getFavouriteRef(userREF, storyREF!!, ::setFavRef)
    }

    private fun setFavRef(ref: Int) {
        _favouriteRef.value = ref
    }

    private fun setScore(scoreRef: Int, value: Float) {
        _scoreRef.value = scoreRef
        _scoreValue.value = value
    }

    private fun setAvgScore(value: Float) {
        _avgScore.value = value
    }

    private fun setBannedWords(bannedWords: MutableList<BannedWord>) {
        _bannedWords.value = bannedWords
    }
}
