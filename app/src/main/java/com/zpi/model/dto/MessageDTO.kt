package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class MessageDTO (
    @SerializedName("message") var message: String? = null
)