package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class AverageDTO (
    @SerializedName("avg") val averageScore: Float? = null
)
