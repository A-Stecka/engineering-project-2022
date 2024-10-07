package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class GenreDTO(
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("value") var genre: String? = null
)