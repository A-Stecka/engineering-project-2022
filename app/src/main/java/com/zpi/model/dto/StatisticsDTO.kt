package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class StatisticsDTO (
    @SerializedName("stories") var stories: Int? = null,
    @SerializedName("comments") var comments: Int? = null,
    @SerializedName("favourites_count") var favourites: Int? = null,
    @SerializedName("favourites_by_other") var favouritesOthers: Int? = null,
    @SerializedName("comments_by_other") var commentsOthers: Int? = null,
    @SerializedName("rated_by_other") var ratedOthers: Int? = null,
    @SerializedName("words_count") var wordsCount: Int? = null,
    @SerializedName("avg_words") var avgWords: Double? = null,
    @SerializedName("streak") var streak: Int? = null,
    @SerializedName("minigame_score") var minigameScore: Int? = null
)