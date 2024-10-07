package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class StoriesPerGenreDTO (
    @SerializedName("genre") var genre: String? = null,
    @SerializedName("stories") var stories: Int? = null
)