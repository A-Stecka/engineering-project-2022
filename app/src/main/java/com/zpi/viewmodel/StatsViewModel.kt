package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zpi.model.entity.Statistics
import com.zpi.model.entity.StoriesPerGenre
import com.zpi.model.service.StatisticsService

class StatsViewModel : ViewModel() {
    private val statisticsService: StatisticsService = StatisticsService()
    private var userRef: Int? = null

    private var _stats: MutableLiveData<Statistics> = MutableLiveData<Statistics>().apply {
        value = Statistics(-1, -1, -1, -1, -1,
            -1, -1, -1.0, -1, -1)
    }

    private var _storiesPerGenre: MutableLiveData<List<StoriesPerGenre>> = MutableLiveData<List<StoriesPerGenre>>().apply {
        value = emptyList()
    }

    val stats: MutableLiveData<Statistics> = _stats
    val storiesPerGenre: MutableLiveData<List<StoriesPerGenre>> = _storiesPerGenre

    fun setUserRef(userRef: Int?) {
        Log.i("StatsViewModel got user", userRef.toString())
        this.userRef = userRef
    }

    fun getUserStatistics() {
        statisticsService.getUserStatistics(userRef!!, ::setStats)
    }

    fun getUserStatisticsPerGenre() {
        statisticsService.getUserStatisticsPerGenre(userRef!!, ::setStoriesPerGenre)
    }

    private fun setStats(stats: Statistics) {
        _stats.value = stats
    }

    private fun setStoriesPerGenre(storiesPerGenre: List<StoriesPerGenre>) {
        _storiesPerGenre.value = storiesPerGenre
    }

}