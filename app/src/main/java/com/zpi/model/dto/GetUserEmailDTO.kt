package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class GetUserEmailDTO(
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("email") var email: String? = null,
)