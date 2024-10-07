package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class ScoreValueDTO (
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("value") var value: Float? = null
)
