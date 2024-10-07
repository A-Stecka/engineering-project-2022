package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class BannedWordDTO (
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("censored") var censored: String? = null
)