package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class PromptDTO (
    @SerializedName("ref") val ref: Int? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("words") val words: String? = null
)