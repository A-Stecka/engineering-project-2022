package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class LeaderboardItemDTO (
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("minigame_score") var minigameScore: Double? = null
)