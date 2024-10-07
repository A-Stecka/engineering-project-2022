package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class GameStoryDTO(
    @SerializedName("content") var content: String? = null,
    @SerializedName("fk_prompt") var fkPrompt: Int? = null,
    @SerializedName("fk_user") var fkUser: Int? = null,
    @SerializedName("genre") var genre: String? = null,
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("reg_date") var regDate: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("words") var words: String? = null,
    @SerializedName("score") var score: Double? = null
)
